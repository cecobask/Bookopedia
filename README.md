# Bookopedia

An app that can be used to keep track of user's book collection. It can search for books using Google Books API using. The app offers a Barcode (ISBN) scanner tool.

When a book is added to the Wish list, the user's is captured, stored and displayed on a Google map, using a custom marker. It will then be displayed when the user accesses that book from his collection.


## Demo
1. Signing in with Google
2. Adding a book to the wish list
3. Deleting a book from the wish list by swiping
4. Previewing a book

![Android Demo](https://media.giphy.com/media/Wn5ZJDxjmJNOZUNEaC/giphy.gif)

## Getting Started

Clone repository:
    
```console
git@github.com:cecobask/Bookopedia.git
```

## Built With

* [Firebase](https://firebase.google.com/) - Persistence, Authentication
* [FirebaseUI](https://github.com/firebase/FirebaseUI-Android) - FirebaseRecyclerAdapter
* [JUnit](https://junit.org/junit4/) - Testing
* [Picasso](https://github.com/square/picasso) - Image downloading
* [AsyncHTTP](https://github.com/loopj/android-async-http) - HTTP client
* [OkHTTP](https://github.com/square/okhttp) - HTTP client
* [AirLocation](https://github.com/mumayank/AirLocation) - Location library
* [Google Maps](https://developers.google.com/maps/documentation/android-sdk/start) - Google maps
* [ZXing](https://github.com/zxing/zxing) - Barcode scanner
* [CircleImageView](https://github.com/hdodenhof/CircleImageView) - Circle Image View
* [ISBNValidator](https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/ISBNValidator.html) - ISBN Validator

## Versioning

[Git](https://git-scm.com/) was used for versioning.

## Running the tests

* Switch Android Studio view to Android
* Right-click app root directory
* Select 'Run All Tests'

## Authors

 **Tsvetoslav Dimov**  
 [LinkedIn](https://www.linkedin.com/in/cecobask/)