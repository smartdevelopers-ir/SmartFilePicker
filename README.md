
[![donate](https://img.shields.io/badge/Donate-Crypto-yellow.svg)](#)
# SmartFilePicker
This is Telegram like File Picker for android .

<img src="files/file%20picker.gif" width="250">

# Installation

* Add it in your root build.gradle at the end of repositories:

``` gradel
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
* Add the dependency

``` gradel
	dependencies {
	        implementation 'com.github.smartdevelopers-ir:SmartFilePicker:1.5.1'
	}
```

# Usage 
* open File Picker
``` java
Bundle extra=new Bundle();
extra.putInt("my_number",10);
Intent intent = new SmartFilePicker.IntentBuilder()
       .showCamera(true)
       .canSelectMultipleInGallery(true)
       .showGalleryTab(true)
       .showPickFromSystemGalleyMenu(true)
       .setExtra(extra)
       .setFileFilter(new SFBFileFilter.Builder().isFile(true).isFolder(true).build())
       .canSelectMultipleInFiles(true)
       .build(this);
startActivityForResult(intent, 10);
```
* Getting result 
``` java
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode==10){
        if (data!=null) {
            Uri[] uris= SmartFilePicker.getResultUris(data);
            if (uris!=null){
               // do somthing with selected files uri
            }
            Bundle extra= SmartFilePicker.getExtra(data);
            if (extra != null) {
               int number = extra.getInt("my_number");
            }
        }
    }
  }
```
