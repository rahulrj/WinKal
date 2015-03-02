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
The Activity that i have used in the sample app is an ActionBarActivity. I have put the "Done" button on it. In general, the activity should have an ActionBar in it in any case, otherwise the central selected element will change its position. 

### Setting as a Fragment
The main Fragment used here is ```DatePickerFragment.``` So to set it up on an Activity( the activity should have an ActionBar in it, just use the following code. The following code will set up the DatePicker with Today's date on it.
```
mDatePickerFragment=new DatePickerFragment();
transaction.replace(R.id.container, mDatePickerFragment);
transaction.commit();
```

Or if you want to pass custom date to set on it, you can put the values in arguments and pass like the following.
```
DatePickerFragment dpFragment = new DatePickerFragment();
Bundle args = new Bundle();
Calendar cal = Calendar.getInstance();
args.putInt(Constants.MONTH, cal.get(Calendar.MONTH) + 1);
args.putInt(Constants.YEAR, cal.get(Calendar.YEAR));
args.putInt(Constants.DATE, cal.get(Calendar.DAY_OF_MONTH));
dpFragment.setArguments(args);

FragmentTransaction t = getSupportFragmentManager().beginTransaction();
t.replace(R.id.container,dpFragment);
t.commit();
```


### Setting as a DialogFragment

### Getting the values back from it

### Customization 

### Handling rotation 



