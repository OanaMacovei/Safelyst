# Safelyst
An app which warns you in live time what to include on the shopping list based on your medical conditions or allergies restrictions. Using Open Food Facts API, the app analyzes product ingredients and nutritional values before adding a product in the shopping list, even when the user is anware of the specific ingredients. Implemented a local database included on the Android system for saving its data, it keeps track of warnings by using a simple statistic format.

-----
<img width="447" height="935" alt="image" src="https://github.com/user-attachments/assets/f381da9f-38f1-4509-95c8-907214292c8b" />
<img width="437" height="942" alt="image" src="https://github.com/user-attachments/assets/59fa1bec-d0a0-448a-850f-4d09147f4e16" />
<img width="445" height="937" alt="image" src="https://github.com/user-attachments/assets/16d72594-4e82-4638-8906-f81ab0bad42e" />
<img width="450" height="946" alt="image" src="https://github.com/user-attachments/assets/570bdace-7cc5-4251-be24-4eb832919e16" />

-----

# Functionalities
1. Shopping List Management
    - CRUD operations for shopping lists and products.
    - Open Food Facts API implementation for fetching product data and ingredients.

2. Health Evaluation System
    - Custom algorithm for keeping track of nutritional scores based on the user's preferances (Diabetes, Hypertension, Cholesterol).
    - Ingredients are automatically checked using multi-language map for words in English as well as French.

3. Warnings & User Interactions
    - The app triggers real time warning dialogs if a product violates profile constrains (eg. showing high-sugar alerts for diabetics).
    - Swipe-to-delete integration allows users to easily remove products or lists and a proper UI to ensure it stays deleted.
    - Undo State Stack: Deleted items can be recovered in a single touch with an Undo button, restoring them in the reverse order they were deleted.

-----

# How to install
  - **Prerequisites**: Ensure you have Android Studio installed.
  - **Setup**: Clone the repository and open the project folder directly in Android Studio.
  - **Run**: Wait for the Gradle sync to finish, select an emulator or a physical device, and press the Run button.

# How to use
1. Launching
    - Once you open, you will see the main dashboard showing your active shopping lists and the user health profile in the top right corner. A detailed look into a list is displayed when you select one.

 2. Shopping Lists
    - Use the plus (+) button to create new lists or enter a list and press its name to edit it. It will save once you press Done button on the keyboard.
    - Inside a shopping list, you can check items off or enter expiration dates.
    - To add products, use the search field at the top, type a product, hit the Search button on your keyboard and dialog with all the fetched products will show up. Warning: Sometimes, the API can have many requests in the same time and an error message will pop up, but don't worry, you cand search again, just keep in mind to try constantly.
 
3. Profile
    - Different medical conditions and allergies can be checked to trigger the system to pay attention to future products.
    - A statistic with the number of warning triggered and a list of maximum 5 products that caused them.

# Future improvements
  - The app to notify the user when the expiration date comes closer and closer and finally when the product expires.
  - Integration of a barcode scanner using the phone's camera for faster product identification.
  - Flexibily and customization for the user to add a product which wasn't found.
