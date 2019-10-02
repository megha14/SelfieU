# #SelfieU
An app that reminds you to take a selfie daily

## Android Application Features
* Basic OAuth Implementation for login functionality
* RecyclerView to list selfies along with the dates created/modified
* Swipe Animations to delete selfies
* Notification for reminders to users to take a selfie
* Notification while syncing with server
* Notification while applying filters
* Screen to view image in full size mode
* Screen to set reminders
* Log in Screen
* Uses SQLite databse to store metadata of selfies
* All the upload/download/filter requests are done on background threads using Services
* Retrofit API interface for client side to connect to server using http.

## Spring Boot Server Features
* Retrofit API interface for server side to connect to server using http.
* Use of Hibernate to store usernames and password
* Futures to handle filter requests so that all requests are handles asynchronously.
