package com.ayushmaanbhav.jstockmart.server;

import java.io.*;

import javax.swing.*;

import com.ayushmaanbhav.jstockmart.user.*;
import com.ayushmaanbhav.jstockmart.utils.TrippleArrayList;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {

	private ServerSocket serverSocket;
	private boolean started;
	private Thread serverThread;
	volatile int clientCount;
	ChatServer chat;
	static StringBuffer chatHist;
	List<Company> companies;
	List<Thread1> clientList;
	volatile static int id = 0;

	abstract class Thread1 extends Thread {
		volatile protected User user;
		abstract public void sendMessage(Object mess, int priority);
	}

	public Server(List<Company> comp) throws IOException {
		companies = comp;
		serverSocket = new ServerSocket(4446, 20000);
		serverSocket.setReuseAddress(true);
		chat = new ChatServer(this);
		clientList = new ArrayList<Thread1>();
		clientCount = 0;
		chatHist = new StringBuffer();
	}

	// This server starts on a seperate thread so you can still do other things
	// on the server program
	public void startServer() {
		if (!started) {
			started = true;
			serverThread = new Thread() {
				public void run() {
					while (Server.this.started) {
						Socket clientSocket = null;
						try {
							clientSocket = serverSocket.accept();
							openClient(clientSocket);
							try {
								Thread.sleep(50);
							} catch (Exception n) {
							}
						} catch (SocketException e) {
							System.err.println("Server closed.");
						} catch (IOException e) {
							System.err.println("Accept failed.");
						}
					}
				}
			};
			serverThread.setPriority(7);
			serverThread.start();
			chat.start();
		}
	}

	@SuppressWarnings("deprecation")
	public void stopServer() {
		this.started = false;
		serverThread.interrupt();
		try {
			serverSocket.close();
		} catch (IOException ex) {
			System.err.println("Server stop failed.");
		}
		chat.stop();
	}

	synchronized public void sendMulti(String mess, int priority) {
		String copy = new String(mess);
		for (int i = 0; i < clientList.size(); i++) {
			if (clientList.get(i).isAlive())
				clientList.get(i).sendMessage(copy, priority);
			else
				clientList.remove(i--);
		}
	}

	synchronized public void sendPeriodicStats(int priority) {
		for (Company comp : ShareMarket.companies) {
			Object message = new TrippleArrayList<Integer, Double, Integer>(Orders.getLimitOrderStatsOfCompany(comp.name));
			for (int i = 0; i < clientList.size(); i++) {
				if (clientList.get(i).isAlive() && clientList.get(i).user != null && clientList.get(i).user.sendStats != null && clientList.get(i).user.sendStats.equalsIgnoreCase(comp.name))
					clientList.get(i).sendMessage(message, priority);
			}
		}
	}

	synchronized public void sendMessage(User user, int priority) {
		for (int i = 0; i < clientList.size(); i++) {
			if (clientList.get(i).user.checkName(user.getName())) {
				if (clientList.get(i).isAlive())
					clientList.get(i).sendMessage(new User(user), priority);
				else
					clientList.remove(i--);
				break;
			}
		}
	}

	public void openClient(final Socket socket) {
		clientCount++;
		Thread1 g = new Thread1() {
			BufferedWriter bw;
			BufferedReader in;
			ObjectOutputStream objout;
			volatile protected boolean loggedIn = false;
			Object obj = new Object();

			public void run() {
				user = null;
				try {
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					objout = new ObjectOutputStream(socket.getOutputStream());
					String inputLine = null;
					do {
						try {
							inputLine = (String) in.readLine();
						} catch (IOException e) {
							break;
						}
						processCommand(inputLine);
					} while (in != null && inputLine != null);
					objout.close();
					in.close();
					socket.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			void processCommand(final String command1) {
				final String cmdid = command1.split(";")[0];
				String command = command1.toLowerCase().split(";")[1];
				String cmmd[] = command.split(":");
				for (int i = 0; i < cmmd.length; i++)
					cmmd[i] = cmmd[i].trim();

				if (cmmd[0].equals(Commands.REGISTER)) {
					int res = UserDatabase.registerUser(cmmd[1], cmmd[2], cmmd[3]);
					if (res == 1) {
						synchronized (obj) {
							try {
								objout.writeObject(new String(cmdid));
								objout.writeObject(new String("1:rs"));
								objout.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						synchronized (obj) {
							try {
								objout.writeObject(new String(cmdid));
								objout.writeObject(new String(res + ":wrn"));
								objout.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					try {
						in.close();
						objout.close();
						socket.close();
						return;
					} catch (Exception e) {
						in = null;
						objout = null;
						return;
					}
				} else if (cmmd[0].equals(Commands.LOG_IN)) {
					int res = UserDatabase.validate(cmmd[1], cmmd[2]);
					if (res == 0 && !UserDatabase.getUser(cmmd[1], cmmd[2]).isBanned()) {
						user = UserDatabase.getUser(cmmd[1], cmmd[2]);
						loggedIn = true;
						try {
							bw = new BufferedWriter(new FileWriter("appdata/userdata/" + user.getName() + ".txt", true));
						} catch (Exception mm) {
						}
						synchronized (obj) {
							try {
								objout.writeObject(new String(cmdid));
								objout.writeObject(new String("0:s"));
								objout.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						try {
							bw.write(new Date().toString() + ":loggedin\n");
							bw.flush();
						} catch (Exception mm) {
							mm.printStackTrace();
						}
						BroadcastServer.sendFullDetails = true;
					} else {
						if (user != null) {
							loggedIn = false;
						}
						synchronized (obj) {
							try {
								objout.writeObject(new String(cmdid));
								objout.writeObject(new String(res + ":f"));
								objout.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						try {
							in.close();
							objout.close();
							socket.close();
							return;
						} catch (Exception e) {
							in = null;
							objout = null;
							return;
						}
					}
				} else if (cmmd[0].equals(Commands.LOG_OUT)) {
					if (user != null) {
						loggedIn = false;
					}
					clientCount--;
					clientList.remove(this);
					synchronized (obj) {
						try {
							objout.writeObject(new String(cmdid));
							objout.writeObject(new String("1"));
							objout.flush();

						} catch (Exception e) {
						}
					}
					try {
						bw.write(new Date().toString() + ":loggedout\n");
						bw.flush();
						bw.close();
						bw = null;
					} catch (Exception mm) {
						mm.printStackTrace();
					}
					try {
						in.close();
						objout.close();
						socket.close();
						return;
					} catch (Exception e) {
						in = null;
						objout = null;
						return;
					}
				} else if (user != null && loggedIn && !user.isBanned()) {
					if (cmmd[0].equals(Commands.GET_USER_DETAILS)) {
						try {
							synchronized (obj) {
								try {
									objout.writeObject(new String(cmdid));
									objout.writeObject(new User(user));
									objout.flush();

									clientList.add(this);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (cmmd[0].equals(Commands.SEND_CHAT_MESSAGE)) {
						// System.out.println("got chat");
						if (user.chatEnabled && user.getChat() >= 0) {
							if (user.getChat() - command1.substring(command1.indexOf(":") + 1).length() >= 0) {
								user.setChat(user.getChat() - command1.substring(command1.indexOf(":") + 1).length());
								chatHist.append("<");
								chatHist.append(user.getName());
								chatHist.append("> : ");
								chatHist.append(command1.substring(command1.indexOf(":") + 1));
								chatHist.append("\n");
								chat.chaat.append("<");
								chat.chaat.append(user.getName());
								chat.chaat.append("> : ");
								chat.chaat.append(command1.substring(command1.indexOf(":") + 1));
								chat.chaat.append("\n");;
								new Thread() {
									public void run() {
										chat.interrupt();
									}
								}.start();
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										StockMart.chat.setText(chatHist.toString());
									}
								});
							}
						}
						synchronized (obj) {
							try {
								objout.writeObject(new String(cmdid));
								objout.writeObject(Integer.toString(user.getChat()));
								objout.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else if (cmmd[0].equals(Commands.GET_CHAT_HISTORY)) {
						synchronized (obj) {
							try {
								objout.writeObject(new String(cmdid));
								objout.writeObject(new String(chatHist.toString()));
								objout.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else if (cmmd[0].equals(Commands.SEND_FULL_BROADCAST)) {
						synchronized (obj) {
							try {
								BroadcastServer.sendFullDetails = true;
								objout.writeObject(new String(cmdid));
								objout.writeObject(new String("1"));
								objout.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else if (cmmd[0].equals(Commands.SEND_PERIODIC_STATS)) {
						try {
							user.sendStats = cmmd[1].trim();
							if (cmmd[1].trim().equals("-1"))
								user.sendStats = null;
						} catch (Exception e) {
							e.printStackTrace();
						}
						synchronized (obj) {
							try {
								objout.writeObject(new String(cmdid));
								objout.writeObject(new String("1"));
								objout.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else if (cmmd[0].equals(Commands.GET_COMPANY_STATS)) {
						try {
							TrippleArrayList<Integer, Double, Integer> arr = Orders.getLimitOrderStatsOfCompany(cmmd[1].trim());
							if (user.sendStats != null)
								user.sendStats = cmmd[1].trim();
							synchronized (obj) {
								try {
									objout.writeObject(new String(cmdid));
									objout.writeObject(new TrippleArrayList<Integer, Double, Integer>(arr));
									objout.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (cmmd[0].equals(Commands.GET_COMPANY_HISTORY)) {
						try {
							Company comp = null;
							for (int k = 0; k < companies.size(); k++) {
								if (companies.get(k).name.equalsIgnoreCase(cmmd[1].trim())) {
									comp = companies.get(k);
									break;
								}
							}
							if (comp != null && Integer.parseInt(cmmd[2].trim()) != comp.sharevalue.size()) {
								synchronized (obj) {
									try {
										objout.flush();
										objout.reset();
										objout.writeObject(new String(cmdid));
										ArrayList<Double> al = new ArrayList<Double>(comp.sharevalue);
										objout.writeObject(al);
										objout.flush();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								synchronized (obj) {
									try {
										objout.writeObject(new String(cmdid));
										objout.writeObject(new ArrayList<Double>());
										objout.flush();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (StockMart.started) {
						if (cmmd[0].equals(Commands.LIMIT_BUY_ORDER)) {
							final Shares share = new Shares();
							share.type = Orders.LIMIT_BUY_ORDER;
							share.company = command1.split(":")[1].trim();
							share.qty_limit = Integer.parseInt(cmmd[2]);
							share.cost_limit = Double.parseDouble(cmmd[3]);
							share.id = id++;
							share.cost = -1.0;
							share.sellid = -1;
							share.buyed = null;
							share.ordered = new Date();
							share.status = "Shares Pending";
							share.notCanceled = true;
							share.user = user;

							Orders.limitBuyOrders.add(share);

							try {
								user.getPendingShares().add(share);
								synchronized (obj) {
									try {
										objout.writeObject(new String(cmdid));
										objout.writeObject(share);
										objout.flush();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								bw.write(new Date().toString() + ":buyl:" + share.id + ":" + share.company + ":" + share.qty_limit + ":" + share.cost_limit + ":" + share.status + "\n");
								bw.flush();
							} catch (Exception mm) {
								mm.printStackTrace();
							}
						} else if (cmmd[0].equals(Commands.MARKET_BUY_ORDER)) {
							final Shares share = new Shares();
							share.type = Orders.MARKET_BUY_ORDER;
							share.company = command1.split(":")[1].trim();
							share.qty_limit = Integer.parseInt(cmmd[2]);
							share.id = id++;
							share.cost = -1.0;
							share.sellid = -1;
							share.buyed = null;
							share.ordered = new Date();
							share.status = "Shares Pending";
							share.notCanceled = true;
							share.user = user;

							// System.out.println("made share");
							Orders.marketBuyOrders.add(share);

							try {
								user.getPendingShares().add(share);
								synchronized (obj) {
									try {
										objout.writeObject(new String(cmdid));
										objout.writeObject(share);
										objout.flush();
										// System.out.println("sent share");
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								bw.write(new Date().toString() + ":buym:" + share.id + ":" + share.company + ":" + share.qty_limit + ":" + share.cost_limit + ":" + share.status + "\n");
								bw.flush();
							} catch (Exception mm) {
								mm.printStackTrace();
							}
						} else if (cmmd[0].equals(Commands.LIMIT_SELL_ORDER)) {
							final Shares share = new Shares();
							share.type = Orders.LIMIT_SELL_ORDER;
							share.company = command1.split(":")[1].trim();
							share.qty_limit = Integer.parseInt(cmmd[2]);
							share.cost_limit = Double.parseDouble(cmmd[3]);
							share.id = Integer.parseInt(cmmd[4]);
							share.sellid = id++;
							share.cost = -1.0;
							share.ordered = new Date();
							share.status = "Payment Pending";
							share.notCanceled = true;
							share.user = user;

							Orders.limitSellOrders.add(share);

							try {
								user.getPendingShares().add(share);
								synchronized (obj) {
									try {
										objout.writeObject(new String(cmdid));
										objout.writeObject(share);
										objout.flush();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								bw.write(new Date().toString() + ":selll:" + share.id + ":" + share.sellid + ":" + share.company + ":" + share.qty_limit + ":" + share.cost_limit + ":" + share.status + "\n");
								bw.flush();
							} catch (Exception mm) {
								mm.printStackTrace();
							}
						} else if (cmmd[0].equals(Commands.MARKET_SELL_ORDER)) {
							final Shares share = new Shares();
							share.type = Orders.MARKET_SELL_ORDER;
							share.company = command1.split(":")[1].trim();
							share.qty_limit = Integer.parseInt(cmmd[2]);
							share.id = Integer.parseInt(cmmd[4]);
							share.sellid = id++;
							share.cost = -1.0;
							share.ordered = new Date();
							share.status = "Payment Pending";
							share.notCanceled = true;
							share.user = user;

							Orders.marketSellOrders.add(share);

							try {
								user.getPendingShares().add(share);
								synchronized (obj) {
									try {
										objout.writeObject(new String(cmdid));
										objout.writeObject(share);
										objout.flush();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								bw.write(new Date().toString() + ":sellm:" + share.id + ":" + share.sellid + ":" + share.company + ":" + share.qty_limit + ":" + share.cost_limit + ":" + share.status + "\n");
								bw.flush();
							} catch (Exception mm) {
								mm.printStackTrace();
							}
						} else if (cmmd[0].equals(Commands.CANCEL_ORDER)) {
							try {
								int id = Integer.parseInt(cmmd[1]);
								int sellid = Integer.parseInt(cmmd[2]);
								Shares sh = null;
								for (int i = 0; i < user.getPendingShares().size(); i++) {
									if (user.getPendingShares().get(i).id == id && user.getPendingShares().get(i).sellid == sellid) {
										sh = user.getPendingShares().get(i);
										break;
									}
								}

								if (sh != null && Orders.contain(sh)) {
									sh.notCanceled = false;
									sh.status = "Cancelled";
									Orders.remove(sh);

									synchronized (obj) {
										try {
											objout.writeObject(new String(cmdid));
											objout.writeObject(new String("1"));
											objout.flush();

										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								} else {
									synchronized (obj) {
										try {
											objout.writeObject(new String(cmdid));
											objout.writeObject(new String("0"));
											objout.flush();

										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}

								try {
									bw.write(new Date().toString() + ":cancel:" + sh.id + ":" + sh.sellid + ":" + sh.company + ":" + sh.qty_limit + ":" + sh.cost_limit + ":" + sh.status + "\n");
									bw.flush();
								} catch (Exception mm) {
									mm.printStackTrace();
								}
							} catch (Exception ef) {
								ef.printStackTrace();
							}
						}
					} else {
						synchronized (obj) {
							try {
								objout.writeObject(new String(cmdid));
								objout.writeObject(new String(Commands.SERVER_NOT_RUNNING));
								objout.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					clientCount--;
					clientList.remove(this);
					try {
						loggedIn = false;
						synchronized (obj) {
							try {
								System.out.println(command1);
								objout.writeObject(new String(Commands.UNKNOWN_COMMAND));
								objout.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						in.close();
						objout.close();
						socket.close();
						user = null;
						return;
					} catch (Exception e) {
						in = null;
						objout = null;
						return;
					}
				}
			}
			boolean firsttime = true;
			BlockingQueue<Object> toSend = new LinkedBlockingQueue<Object>();
			Thread sender;
			public void sendMessage(Object mess, int priority) {
				try {
					sender.setPriority(priority);
				} catch (Exception m) {
				}
				toSend.offer(mess);
				// System.out.println("offered: " + mess.toString());
				if (firsttime && loggedIn) {
					sender = new Thread() {
						public void run() {
							while (loggedIn) {
								Object object = null;
								try {
									object = toSend.take();
								} catch (Exception e) {
									e.printStackTrace();
								}
								if (object != null) {
									synchronized (obj) {
										try {
											objout.writeObject(object);
											objout.flush();
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
								try {
									Thread.sleep(100);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					};
					sender.setPriority(Thread.NORM_PRIORITY);
					sender.start();
					firsttime = false;
				}
			}
		};
		g.setPriority(Thread.NORM_PRIORITY);
		g.start();
	}
}