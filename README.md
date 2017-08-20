In this project we used AsyncHttpClient library to upload file from gallery or take a photo from camera.

1: You add the compile 'com.loopj.android:android-async-http:1.4.9' in 'app.gradle' file.

2: Add below lines in manifest file:

  <uses-permission android:name="android.permission.INTERNET" />
  
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
  
  <uses-permission android:name="android.permission.CAMERA" />
  
3: Open MainActivity in this project and use all codes in this file.

Enjoy it!
