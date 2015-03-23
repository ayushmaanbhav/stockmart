package com.ayushmaanbhav.jstockmart.user;

public class Commands {
	// client commands
	public static final String LOG_IN = "login";
	public static final String REGISTER = "reg";
	public static final String LOG_OUT = "logout";
	public static final String GET_USER_DETAILS = "gud";
	public static final String SEND_CHAT_MESSAGE = "chat";
	public static final String GET_CHAT_HISTORY = "chath";
	public static final String BUY = "buy";
	public static final String SELL = "sell";
	public static final String LIMIT_ORDER = "limit";
	public static final String MARKET_ORDER = "market";
	public static final String MARKET_BUY_ORDER = BUY + MARKET_ORDER;
	public static final String MARKET_SELL_ORDER = SELL + MARKET_ORDER;
	public static final String LIMIT_BUY_ORDER = BUY + LIMIT_ORDER;
	public static final String LIMIT_SELL_ORDER = SELL + LIMIT_ORDER;
	public static final String CANCEL_ORDER = "cancel";
	public static final String GET_COMPANY_HISTORY = "getch";
	public static final String GET_COMPANY_STATS = "getcs";
	public static final String SEND_PERIODIC_STATS = "sendps";
	public static final String SEND_FULL_BROADCAST = "sendfb";

	// server commands
	public static final String BROADCAST = "broadcast";
	public static final String CHAT_AND_NEWS = "chat";
	public static final String FINAL_RANKINGS = "rankings";
	public static final String RANKS = "rank";
	public static final String UNKNOWN_COMMAND = "-101";
	public static final String SERVER_NOT_RUNNING = "-102";
}
