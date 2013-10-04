# Barcamp Bangalore Android App

This is the Android app I had created for Barcamp Bangalore. The live version of the same is available at https://market.android.com/details?id=com.bangalore.barcamp All icons and design are credited to @amanmanglik(http://twitter.com/amanmanglik), code is available under APL. For more info read the LICENCE file.

## Aim

App for Barcamp Bangalore Monsoon 2013. It allows you to.

1. View Barcamp sessions schedule
2. If you login with your Barcamp Website, you can sync the sessions you marked as attending to the app.
3. Setup reminders for the talks which you want to attend
4. View all tweets related to Barcamp Bangalore
5. View all updates from BCB team sent via push notification
6. Venue location for Barcamp Bangalore
7. Internal Venue Map for Barcamp Bangalore
8. Share from inside the app so that you dont have to add the hashtag

# Dependency

Barcamp Bangalore App makes use of following libraries. Please get them and include them as library projects in your eclipse projects

* Android Actionbar - https://github.com/johannilsson/android-actionbar
* Android RSS - https://github.com/ahorn/android-rss/
* Android Sliding menu - https://bitbucket.org/jfeinstein10/slidingmenu/src

# Schedule JSON Format

Look into example barcampdata.json file in the same folder

# Want to fork for your Barcamp ? 

Go ahead do it. 

* Search and replace BCB and BCB with your tag. 
* Also replace the link to get the xml from barcampbangalore's website to your own. 
* Change the notification message location from barcampbangalore twitter stream to your own
* Change the venue to your own.
* Drop in a mail at [Barcamp Bangalore Mailing list](http://tech.groups.yahoo.com/group/bangalore_barcamp/) or tweet to @barcampbng(http://twitter.com/barcampbng)

# Want to contribute?

GitHub has some great articles on [how to get started with Git and GitHub](http://help.github.com/) and how to [fork a project](http://help.github.com/forking/).

Contributers are recommended to fork the app on GitHub (but don't have too). Create a feature branch, push the branch to git hub, press Pull Request and write a simple explanation.

One fix per commit. If say a a commit closes the open issue 12. Just add `closes #12` in your commit message to close that issue automagically.

All code that is contributed must be compliant with [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

For any questions that you have regards to this pleas shoot in a mail at Barcamp Bangalore Mailing List(http://tech.groups.yahoo.com/group/bangalore_barcamp/)

## Code Style Guidelines

Contributers are recommended to follow the Android [Code Style Guidelines](http://source.android.com/source/code-style.html) with exception for line length that I try to hold to 80 columns where possible.

In short that is;

* Indentation: tabs, no spaces
* Line length: 80 columns
* Braces: Opening braces don't go on their own line.
* Acronyms are words: Treat acronyms as words in names, yielding XmlHttpRequest, getUrl(), etc.
* Consistency: Look at what's around you!

Have fun and remember we do this in our spare time so don't be too serious :)

# License
Copyright (c) 2013 [Saurabh Minni](http://100rabh.com)

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
One extra thing that we want you do is, let the sliding menu have the message "Originally made for Barcamp bangalore" intact. Though you may shift it anywhere you may want to if your design so warrants.


