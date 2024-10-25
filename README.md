# Tourist Guide App -  Built with Jetpack Compose

This project involves the development of an Android application aimed at facilitating tourism visits to various locations by providing relevant information on points of interest. The app is designed to accommodate a variety of location types such as cities, beaches, mountains, islands, and regions.

## Features

- **Location Search & Sorting:**
  - Users can select a location from a list, which can be sorted alphabetically or by proximity to their current position.
  
- **Points of Interest:**
  - After selecting a location, users can view a list of points of interest.
  - The list can be filtered by categories such as Museums, Monuments & Places of Worship, Gardens, Viewpoints, Restaurants & Bars, and Accommodation.
  - Points of interest can be sorted alphabetically or by distance.

- **User Contributions:**
  - Users can add new locations, categories, and points of interest.
  - Each location and point of interest must include a descriptive text and at least one photo.
  - New categories require an image or icon and a brief description.

- **Geolocation:**
  - Locations and points of interest are georeferenced, requiring latitude and longitude coordinates.
  - Coordinates are automatically obtained from the user's device or can be manually input.
  - The accuracy of the coordinates is displayed, with the precision shown via text or icons.

- **Community Validation:**
  - New contributions are only published after approval by two users.
  - Pending approval items are clearly marked as such, allowing users to review and validate them.
  - Edited contributions require re-approval.

- **Ratings and Reviews:**
  - Users can rate points of interest on a scale from 0 to 3.
  - Each user can add a single photo and a comment with a maximum of 200 characters.

- **Item Removal:**
  - Points of interest can be deleted by their authors but require approval from three users if they have already been rated.
  - Locations and categories can only be deleted if they are not linked to any points of interest.

## Development

The app was developed using **Jetpack Compose** for the UI.

All data is stored and shared via **Firebase** services, ensuring real-time synchronization between users. Firebase Authentication handles user registration and login, ensuring the traceability of all user actions, such as adding, rating, and commenting on points of interest.

The app uses **Firestore**, and **Firebase Storage** for storing and retrieving data efficiently.

## Technologies Used

- **Android**: Jetpack Compose
- **Backend**: Firebase (Firestore, Storage)
- **Authentication**: Firebase Authentication
