package pt.isec.amovtp.touristapp.ui.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pt.isec.amovtp.touristapp.R
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel

enum class Screens(val display: String, val showAppBar : Boolean) {
    MENU("Menu", false),
    LOGIN("Login", false),
    REGISTER("Register", true),
    LOCATIONS("Locations", true),
    ADD_LOCATIONS("Add Locations", true),
    EDIT_LOCATION("Edit Locations", true),
    POI("Point of Interest", true),
    ADD_POI("Add Points of Interest", true),
    EDIT_POI("Edit Points of Interest", true),
    POI_DESCRIPTION("Point of Interest Description", true),
    ADD_CATEGORY("Add Category", true),
    LIST_CATEGORY("List Category", true),
    EDIT_CATEGORY("Edit Category", true),
    CREDITS("Credits", true),
    ADD_COMMENTS("Add Comments", true),
    ADD_POI_PICTURES("Add POI Pictures", true);

    val route : String
        get() = this.toString()
}


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController:NavHostController = rememberNavController(),
               locationViewModel: LocationViewModel,
               firebaseViewModel: FirebaseViewModel
) {
    var showAdd by remember { mutableStateOf(false) }

    val currentScreen by navController.currentBackStackEntryAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val configuration = LocalConfiguration.current

    navController.addOnDestinationChangedListener {
        controller, destination, arguments ->
        showAdd = (destination.route in arrayOf(
            Screens.LOCATIONS.route, Screens.POI.route
        ))
    }

    Scaffold (
        snackbarHost = { SnackbarHost (hostState = snackbarHostState) },
        topBar = {
            if(currentScreen != null && Screens.valueOf(currentScreen!!.destination.route!!).showAppBar) {
                CenterAlignedTopAppBar(
                    title = { Text(
                        text = when (currentScreen!!.destination.route) {
                            Screens.LOCATIONS.route -> stringResource(R.string.msgLocations)
                            Screens.ADD_LOCATIONS.route -> stringResource(R.string.msgAddLocation)
                            Screens.ADD_CATEGORY.route -> stringResource(R.string.msgAddCategory)
                            Screens.ADD_COMMENTS.route -> stringResource(R.string.msgComments)
                            Screens.ADD_POI_PICTURES.route -> stringResource(R.string.msgAddPOIPicture)
                            Screens.ADD_POI.route -> stringResource(R.string.msgAddPOI)
                            Screens.CREDITS.route -> stringResource(R.string.btnCredits)
                            Screens.EDIT_POI.route -> stringResource(R.string.msgEditPOI)
                            Screens.EDIT_LOCATION.route -> stringResource(R.string.msgEditLocation)
                            Screens.EDIT_CATEGORY.route -> stringResource(R.string.msgEditCategory)
                            Screens.LIST_CATEGORY.route -> stringResource(R.string.msgCategories)
                            Screens.POI.route -> stringResource(R.string.msgPOIS)
                            else -> {
                                stringResource(R.string.app_name)
                            }
                        },
                        color = MaterialTheme.colorScheme.tertiary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp()
                        }) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                    ),
                )
            }
        },
        floatingActionButton = {
            if(currentScreen != null && (Screens.valueOf(currentScreen!!.destination.route!!) == Screens.POI ||
                        Screens.valueOf(currentScreen!!.destination.route!!) == Screens.LOCATIONS))
            FloatingActionButton(
                onClick = {
                    if(Screens.valueOf(currentScreen!!.destination.route!!) == Screens.POI)
                        navController.navigate(Screens.ADD_POI.route)
                    else
                        navController.navigate(Screens.ADD_LOCATIONS.route)
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }

            if(currentScreen != null && Screens.valueOf(currentScreen!!.destination.route!!) == Screens.MENU)
                FloatingActionButton(
                onClick = {
                    firebaseViewModel.signOut()
                    navController.navigate(Screens.LOGIN.route) {
                        popUpTo(Screens.LOGIN.route) { inclusive = true }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = stringResource(R.string.msgLogout)
                    )
                }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = Screens.LOGIN.route,
            modifier = Modifier.padding(it)
        ) {
            composable (Screens.MENU.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapeMenuScreen( navController, firebaseViewModel, Screens.LOCATIONS.route, Screens.CREDITS.route) }
                    else -> MenuScreen( navController, firebaseViewModel)
                }
            }
            composable (Screens.LOGIN.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> LandscapeLoginScreen(navController, firebaseViewModel) {
                        firebaseViewModel.getUserFromFirestore(firebaseViewModel.authUser.value!!.uid)
                        navController.navigate(Screens.MENU.route)
                    }
                    else -> LoginScreen(navController, firebaseViewModel) {
                        firebaseViewModel.getUserFromFirestore(firebaseViewModel.authUser.value!!.uid)
                        navController.navigate(Screens.MENU.route)
                    }
                }
            }
            composable (Screens.REGISTER.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapeRegisterScreen(navController, firebaseViewModel) }
                    else -> RegisterScreen(navController, firebaseViewModel)
                }
            }
            composable (Screens.LOCATIONS.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapeLocationsScreen(navController = navController, viewModel = locationViewModel, firebaseViewModel = firebaseViewModel) }
                    else -> LocationsScreen(navController = navController, viewModel = locationViewModel, firebaseViewModel = firebaseViewModel)
                }
            }
            composable (Screens.ADD_LOCATIONS.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapeAddLocationScreen( navController = navController, locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel) }
                    else -> AddLocationScreen( navController = navController, locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel)
                }
            }
            composable (Screens.EDIT_LOCATION.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapeEditLocationScreen(navController = navController, locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel) }
                    else -> EditLocationScreen(navController = navController, locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel)
                }
            }
            composable (Screens.POI.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapePOIScreen(navController = navController, viewModel = locationViewModel, firebaseViewModel = firebaseViewModel) }
                    else -> POIScreen(navController = navController, viewModel = locationViewModel, firebaseViewModel = firebaseViewModel)
                }
            }
            composable (Screens.ADD_POI.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapeAddPOIScreen( navController = navController, locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel) }
                    else -> AddPOIScreen( navController = navController, locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel)
                }
            }
            composable (Screens.EDIT_POI.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapeEditPOIScreen(navController = navController, locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel) }
                    else -> EditPOIScreen(navController = navController, locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel)
                }
            }
            composable (Screens.POI_DESCRIPTION.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapePOIDescriptionScreen(modifier = Modifier, viewModel = locationViewModel, firebaseViewModel = firebaseViewModel) }
                    else -> POIDescriptionScreen(modifier = Modifier, viewModel = locationViewModel, firebaseViewModel = firebaseViewModel)
                }
            }
            composable (Screens.LIST_CATEGORY.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapeListCategoryScreen(navController = navController, modifier = Modifier, viewModel = locationViewModel, firebaseViewModel = firebaseViewModel) }
                    else -> ListCategoryScreen(navController = navController, modifier = Modifier, viewModel = locationViewModel, firebaseViewModel = firebaseViewModel)
                }
            }
            composable (Screens.EDIT_CATEGORY.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapeEditCategoryScreen(navController = navController, modifier = Modifier, locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel) }
                    else -> EditCategoryScreen(navController = navController, modifier = Modifier, locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel)
                }
            }
            composable (Screens.ADD_CATEGORY.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapeAddCategoryScreen(modifier = Modifier, navController = navController, firebaseViewModel = firebaseViewModel) }
                    else -> AddCategoryScreen(modifier = Modifier, navController = navController, firebaseViewModel = firebaseViewModel)
                }
            }
            composable (Screens.ADD_COMMENTS.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapeAddCommentsScreen(locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel) }
                    else -> AddCommentsScreen(locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel)
                }
            }
            composable (Screens.ADD_POI_PICTURES.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapeAddPOIPicturesScreen(locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel) }
                    else -> AddPOIPicturesScreen(locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel)
                }
            }
            composable (Screens.CREDITS.route) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> { LandscapeCreditsScreen() }
                    else -> CreditsScreen()
                }
            }
        }
    }
}