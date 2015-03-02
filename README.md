# WinKal
I noticed someday when i was working on a windows Phone app that what awesome [Date and Time Picker](http://www.geekchamp.com/articles/wp7-datepicker-and-timepicker-in-depth--api-and-customization) Windows  
Phone has as compared to Android. That time i still had Android 4.0 and 2.3 and neither of these have good date and time pickers.
But now Google has released a very nice DTPickers and Flavien Laurent has made a [library](https://github.com/flavienlaurent/datetimepicker) for it. But i 
wanted to see the Windows DTPicker in Android and here it is.  
    
It can be customized and used with the cool animations of Material Designs to produce a really nice DTPicker UI.  

  
## Demo  

![](http://winkal.16mb.com/winkal_cmp.mp4)


## Usage  
#### Gradle

For now, its not hosted anywhere. So for including it in the project, import it in Android Studio and in the working
project,specify ```compile project(':WinKal')``` in the build.gradle file.

#### Eclipse


## How To Use?

The picker can be used as a ```Fragment``` or as a ```DialogFragment.```
The main Fragment used here is ```DatePickerFragment```. So to set it up on an Activity, just use the following code. The following code will set up the DatePicker with Today's date on it.
 ```mDatePickerFragment=new DatePickerFragment();
        if(savedInstanceState!=null){

            mDatePickerFragment.restoreStatesFromKey(savedInstanceState,"CALENDAR_SAVED_STATE");
        }

transaction.replace(R.id.container, mDatePickerFragment);
transaction.commit();```




