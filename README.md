# Bidding Server Client Network

This project demonstrates a server-client architecture. The goal is to design and implement an
online bidding/auction server and graphical user interface (GUI) client..

All communication between the server and clients will occur through sockets, therefore the program will
work even if server and client are not run on same machine.

The GUI client was implemented using the JavaFX Library. This allows users to send/receive data through
the use of JSON requests and responses.

The Server handles each clients' request by accessing an SQL Database. This is done by interacting with
a MySQL Connector that permits the execution of SQL code in java, which is implemented in the server
through a library of functions.


## Server Side:

1. As it starts up, the server reads all items from the SQL Database containing their relevant data.

2. As customers place valid bids on a particular item, the server updates the prices and informs all
active customers of the new highest bid on that item.

3. The server handles multiple auction items and customers concurrently.

4. Concurrent bidding on the items is allowed. Server employs proper synchronization so that multiple
clients can bid on the same item at the same time in a thread-safe way.

5. Console will be used to output logs of the server (status and user information).

## Client Side:

1. The client provides each customer with an appealing JavaFX graphical user interface.

2.  Every client (user) gets a start-up window where they log in with a user name and password, or
log in as guest.

3. After logging in, clientsselect any item to bid on and place a bid, which is sent to the server.

4. Clients are notified of all bids placed by all customers during their login session and view a
history of bids during their session.

5. The client is constantly informed whether an iterm can be bid upon further, their current bid is
invalid, or whether the auction for that item is closed. If that item was sold, the winning client's
username is shown along with the price of the final bid.

6. The window to every client also provides "quit", "logout", and "exit" buttons, which disconnects
clients appropriately and gracefully.
