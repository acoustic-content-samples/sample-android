# sample-android
This sample provides the source for a Native Android application that illustrates how to access Acoustic Content (formerly Watson Content Hub) Authoring APIs from Android, such as how to login to a tenant, retrieve the taxonomies, fetch content items, and filter content items by categories.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Running the sample](#running-the-sample)
3. [WCH functions](#wch-functions)
  * [Get Taxonomies](#get-taxonomies)
  * [Get Categories](#get-categories)
  * [Get Content Items](#get-content-items)

## Prerequisites
* Android Studio
* Android phone or emulator
* Minimum SDK: 19 (KitKat)

[back to top](#sample-android)

## Running the sample
1. Download the files
  * Clone or download this repository and import the folder WCH_Sample_Android into Android Studio
2. Update the baseTenantAPIURL
  * The baseTenantAPIURL variable in app/src/main/java/com.ibm.wch_sample_android/LoginActivity.java must be set for your tenant. In the IBM Watson Content Hub user interface, open the user menu from the top navigation bar, then select "Hub information". The pop-up window shows your API URL, host and content hub ID for your Watson Content Hub tenant. Use this information to update the value of the baseTenantAPIURL variable in app/src/main/java/com.ibm.wch_sample_android/LoginActivity.java, in the form https://{host}/api/{content hub tenant id}. For example it might look something like this: const baseTenantAPIURL = "https://my12.digitalexperience.ibm.com/api/12345678-9abc-def0-1234-56789abcdef0";
3. Run on emulator or Android device
  * Build the project
  * [Tips for running app on a device](https://developer.android.com/studio/run/device.html)
  
    or alternatively
  * Click run button and select an emulator to run the app
4. Navigating the app
  * The first activity is the login. Type the credentials of a user associated with the tenant and hit "SIGN IN"
  * ![LoginActivity](/Wiki/img/login.PNG)
  * If the user credentials are correct, the home activity will open. The baseUrl and TenantId are displayed at the top of the app. Make sure those are correct.
  * ![HomeActivity](/Wiki/img/home.PNG)  
  * From here, you can hit "GET TAXONOMY NAMES." This will grab all the existing taxonomies and display their names.
  * ![Taxonomies](/Wiki/img/taxonomies.PNG)
  * Next, type one of the taxonomy names into the appropriate text field and hit "GET CONTENT ITEMS." This will return all existing content items of this taxonomy.
  * ![Get Categories](/Wiki/img/get_by_taxonomy.PNG)
  * Further, you can filter content items by a specific category of a taxonomy. After adding the taxonomy name, hit "GET CATEGORIES" to retrieve all categories under the specific taxonomy.
  * From there, you can add a category name to the appropriate text field and hit "GET CONTENT ITEMS" again. This time, only items of that specific category will be displayed.
  * ![Get Categories](/Wiki/img/get_by_taxonomy_category.PNG)

[back to top](#sample-android)

## WCH functions
Here is a list of all the interactions with the WCH API that are illustrated in this sample. The functions' behavior follow the same pattern:

1. Initialize a Response.Listener and a Response.ErrorListener, which define how the app should respond for a successful and unsuccessful request respectively.
2. On success, grab the JSON response as a JSONObject and traverse the JSONObject tree to reach desired data. On error, display the appropriate message.
3. Update Activity

### Get Taxonomies
Prints the names of the taxonomies of the tenant in a list as a String
* Parameters: none

### Get Categories
Prints the names of the categories for the specified taxonomy of the tenant in a list as a String
* Parameters: taxonomy name

### Get Content Items
Prints the name, type, categories, and date modified for each content item of the specified taxonomy (and category) of the tenant in a list 
* Parameters: taxonomy name (and category name)

[back to top](#sample-android)
