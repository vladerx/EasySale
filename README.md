instructions:

the app interacts with the server's api, by requesting REGISTER, LOGIN, LIST USERS, CREATE, UPDATE.
DELETE doesnt used because the api returns only an error. 
normally i would fetch data from api and save it in the database by using the repository
and observe changes and update fragments/activities. but since data cannot be added, deleted or updated
on server side i just used the api to get responses to simulate interation with it.

login and register pages both made just for good practice to simulate interactions with API, 
callback data doesnt used, since nothing really created.
register and login requires email field: eve.holt@reqres.in,  password: any.

on the first use of the app, it fetches list of users from api, returned values saved into database,
displayed on recyclerview and determine how many pages and how many users per page displayed.
since the api itself doesnt create users, there is no point petching users from the api again,
so after the data fetched, on consecutive uses it only uses the database to fetch users.

next and back arrows in the buttom bar used to switch pages, the bar updates if a user added
or deleted, all changes reflected also in database and on recyclerview that displays
the current page's users on cardviews.
in top bar there are a search box to type a phrase and and magnifying glass to execute search,
if more than one result is found pressing on next arrow moves to the next result.

on the buttom right corner there is a button to create new user. in a new fragment it requires to fill
3 fields and optionally upload image, images saved as strings. creating sends requests to api
with constant name and job (just for good practice), and on callback user is created and saved in the database.
the location of newly created user is based on free space on each page if there is no free space
new page is made.

pressing on an item in recyclerview, opens new fragment and sends request to fetch a user. since there 
is no user creation the api limited only to original user list, so for convenience it sends the same 
data just for good practice. on successful callback the fragment displays the user's info for editing,
same as create fragment it sends an api request before it updates a user.
swiping right a cardview in recyclerview, gets a delete alert message if confiremed a card is deleted
from list.if no users left in the page, the pages always adjust to have no empty pages, unless
no users left.
long press on the cardview allows to move users in the page.
