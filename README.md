StockMart
=========

A java based Virtual Stock Exchange Game (VSE)<br>
Want to know more about Stock Markets? Here: https://www.youtube.com/watch?v=F3QpgXBtDeo

<h2>Description:</h2>
I developed the application in two parts, a client applet which interacts with a server application via TCP. The features of the client applet include live ranking, chat discussion, news items, company stocks and their market values, the status of share market and sensex. The features of the admin application (server) include live view of the users, their details, stocks and money, set news items, set time limit, block chat, delete user, view rankings, chat discussion and edit stock prices.

<h2>Features and Improvements (Branch: real-bid):</h2>
Introduced real bidding whereas the code in the master branch operates on Monte Carlo simulation and there is no real bidding. Introduced many new features like bid graphs and order queue graphs !!! Fixed many bugs.... Developed a matching engine for matching orders or bids..... Improved code structure....

<h2>Guidelines</h2>
<h3>Server</h3>
* money.txt: specify user's initial money. user will get that money and some random shares of the same value.<br>
* companies.txt: specify the companies and their initial share value just after their name, separated by a ":", ignore rest of the values. eg:<br>
"Reliance:260"<br>
(one company per line)<br>
* reg.txt: if only user with allotted regnos are to be able to play then specify the registration nos in this file, (only small chars and numbers), you can disable the regno. checking by clicking on the button "CheckREG" on the main screen.<br>
* Start server by double clicking it, set the time limit, wait for the users to connect, finally start the server by clicking the start button...<br>
* You can add spice to the game by using the text area below to show news to the users. "ONE NEWS ITEM PER LINE". News about companies to fluctuate the prices. :) Eg. of a positive news:<br>
"Microsoft announces Windows 10. This new version will thrill the users."<br>
* You can view users in the user panel, increase their money, give them bonus, increase their chat, change their pass, ban and delete them, view their stocks and orders... etc<br>
* You can view the pending orders in the order queue and manually cancel them if necessary or u face any problem.

<h3>Client:</h3>
* Start client by cmd prompt or terminal by giving ip of the server as the first argument eg:<br>
"java -jar StockMartClient.jar -localhost"<br>
* Register (each field is restricted to use only small chars and nos.), login and start playing.

<h2>Screenshots:</h2>
<h3>Server:</h3>
<img src="https://cloud.githubusercontent.com/assets/4680789/6806859/49115ab2-d271-11e4-825e-41dfd4e2e7f8.PNG">
<img src="https://cloud.githubusercontent.com/assets/4680789/6806858/48f789b6-d271-11e4-97ab-c82c5c968cba.PNG">
<img src="https://cloud.githubusercontent.com/assets/4680789/6806861/4943f0a8-d271-11e4-8420-e7d70787b33d.PNG">
<img src="https://cloud.githubusercontent.com/assets/4680789/6806860/491cf318-d271-11e4-8838-d64e31166f01.PNG">

<h3>Client:</h3>
<img src="https://cloud.githubusercontent.com/assets/4680789/6806856/486c49fa-d271-11e4-8a67-67826f90efe5.PNG">
<img src="https://cloud.githubusercontent.com/assets/4680789/6806853/485f95e8-d271-11e4-95fb-c4f9beb67c29.PNG">
<img src="https://cloud.githubusercontent.com/assets/4680789/6806854/48645f4c-d271-11e4-9c73-1d42545a5c93.PNG">
<img src="https://cloud.githubusercontent.com/assets/4680789/6806855/486b8c9a-d271-11e4-94a5-20b15fef0a76.PNG">
<img src="https://cloud.githubusercontent.com/assets/4680789/6806857/48c4131a-d271-11e4-8a39-da2d3f5a904d.PNG">
