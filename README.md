
[![donate](https://img.shields.io/badge/Donate-Crypto-yellow.svg)](https://smartdevelopers-ir.github.io/donate)
![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/smartdevelopers-ir/SmartFilePicker)
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
	        implementation 'com.github.smartdevelopers-ir:SmartFilePicker:2.0.8'
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
* Chaging Theme
  
  To change theme you should override this style to extends your AppTheme
  
  ``` xml
   <style name="App.AppTheme" parent="Theme.Material3.Light.NoActionBar">
  	...
  </style>
  <style name="SFB.Base" parent="App.AppTheme"/>
  ```
  
  And for customizing bottomNavigation or bottons color you should override this style :
  
  ``` xml
	<style name="SFB.Base.Theme.Browser" parent="SFB.Base.Theme">
         <item name="SFBColorGallery">@color/sfb_color_gallery</item>
        <item name="SFBColorFile">@color/sfb_color_file</item>
        <item name="SFBColorAudio">@color/sfb_color_audio</item>
        <item name="SFBColorPDF">@color/sfb_color_pdf</item>
        <item name="SFBBottomNavActiveColor">@color/white</item>
        <item name="SFBBottomNavInactiveColor">@color/sfb_color_item_inactive</item>
        <item name="SFBBottomNavColorDisabled">@color/sfb_color_item_disabled</item>
        <item name="SFBCheckboxFillColor">?attr/colorSecondary</item>
        <item name="SFBCheckboxOnFillColor">?attr/colorOnSecondary</item>
        <item name="SFBCheckboxStrokeColor">?attr/colorOnSecondary</item>
    </style>
  ```
# Donation
You can support me by donating with cryptocurrency :)

[I glad to support you](https://smartdevelopers-ir.github.io/donate)
