package com.example.bitewise.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// enum class IngredientSelectionMode { // Assuming this is defined elsewhere or at top
// SEARCH_FILTER,
// AUTO_GENERATE_PLAN
// }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientSelectionView(
    onSearch: (List<String>) -> Unit,
    onBack: () -> Unit,
    mode: IngredientSelectionMode
) {
    // Full list of ingredients from the JSON response
    val jsonIngredients = listOf(
        "Ackee", "Allspice", "Almond Extract", "Almond Milk", "Almonds", "Ancho Chillies",
        "Anchovy Fillet", "Apple Cider Vinegar", "Apples", "Apricot", "Apricot Jam",
        "Asparagus", "Aubergine", "Avocado", "Baby Aubergine", "Baby Plum Tomatoes",
        "Baby Squid", "Bacon", "Baguette", "Baked Beans", "Baking Powder",
        "Balsamic Vinegar", "Banana", "Barbeque Sauce", "Basil", "Basil Leaves",
        "Basmati Rice", "Bay Leaf", "Bay Leaves", "Bean Sprouts", "Beef", "Beef Brisket",
        "Beef Fillet", "Beef Gravy", "Beef Kidney", "Beef Shin", "Beef Stock",
        "Beef Stock Concentrate", "Beetroot", "Bicarbonate Of Soda", "Biryani Masala",
        "Black Beans", "Black Olives", "Black Pepper", "Black Pudding", "Black Treacle",
        "Blackberries", "Blue Food Colouring", "Blueberries", "Boiling Water",
        "Borlotti Beans", "Bouquet Garni", "Bowtie Pasta", "Braeburn Apples",
        "Bramley Apples", "Brandy", "Bread", "Bread Rolls", "Breadcrumbs", "Brie",
        "Broad Beans", "Broccoli", "Brown Lentils", "Brown Rice", "Brown Sugar",
        "Brussels Sprouts", "Buckwheat", "Bulgur Wheat", "Bun", "Buns", "Butter",
        "Butter Beans", "Butternut Squash", "Cabbage", "Cacao", "Cajun", "Candied Peel",
        "Canola Oil", "Canned Tomatoes", "Cannellini Beans", "Capers", "Caramel",
        "Caramel Sauce", "Caraway Seed", "Cardamom", "Carrots", "Cashew Nuts", "Cashews",
        "Caster Sugar", "Cayenne Pepper", "Celeriac", "Celery", "Celery Salt", "Challots",
        "Charlotte Potatoes", "Cheddar Cheese", "Cheese", "Cheese Curds", "Cheese Slices",
        "Cherry", "Cherry Tomatoes", "Chestnut Mushroom", "Chestnuts", "Chicken",
        "Chicken Breast", "Chicken Breasts", "Chicken Legs", "Chicken Stock",
        "Chicken Stock Concentrate", "Chicken Stock Cube", "Chicken Thighs", "Chickpeas",
        "Chili Powder", "Chilled Butter", "Chilli", "Chilli Powder", "Chinese Broccoli",
        "Chives", "Chocolate Chips", "Chopped Onion", "Chopped Parsley",
        "Chopped Tomatoes", "Chorizo", "Christmas Pudding", "Ciabatta", "Cider",
        "Cilantro", "Cinnamon", "Cinnamon Stick", "Clams", "Clotted Cream", "Cloves",
        "Coco Sugar", "Cocoa", "Coconut Cream", "Coconut Milk", "Cod", "Colby Jack Cheese",
        "Cold Water", "Condensed Milk", "Cooking wine", "Coriander", "Coriander Leaves",
        "Coriander Seeds", "Corn Flour", "Corn Tortillas", "Cornstarch", "Courgettes",
        "Couscous", "Cream", "Cream Cheese", "Creamed Corn", "Creme Fraiche", "Crusty Bread",
        "Cubed Feta Cheese", "Cucumber", "Cumin", "Cumin Seeds", "Currants", "Curry Powder",
        "Custard", "Custard Powder", "Dark Brown Soft Sugar", "Dark Brown Sugar",
        "Dark Chocolate", "Dark Chocolate Chips", "Dark Rum", "Dark Soy Sauce",
        "Demerara Sugar", "Desiccated Coconut", "Diced Tomatoes", "Digestive Biscuits",
        "Dijon Mustard", "Dill", "Dill Pickles", "Doner Meat", "Doubanjiang",
        "Double Cream", "Dried Apricots", "Dried Fruit", "Dried Oregano", "Duck", "Duck Fat",
        "Duck Legs", "Duck Sauce", "Egg", "Egg Plants", "Egg Rolls", "Egg White", "Egg Yolks",
        "Eggs", "Enchilada Sauce", "English Muffins", "English Mustard",
        "Extra Virgin Olive Oil", "Fajita Seasoning", "Farfalle", "Fennel", "Fennel Bulb",
        "Fennel Seeds", "Fenugreek", "Fermented Black Beans", "Feta", "Fettuccine", "Fideo",
        "Figs", "Filo Pastry", "Fish Stock", "Flaked Almonds", "Flax Eggs", "Flour",
        "Flour Tortilla", "Floury Potatoes", "Free-range Egg, Beaten",
        "Free-range Eggs, Beaten", "French Lentils", "Fresh Basil", "Fresh Thyme",
        "Freshly Chopped Parsley", "Fries", "Fromage Frais", "Frozen Peas", "Fruit Mix",
        "Full Fat Yogurt", "Garam Masala", "Garlic", "Garlic Clove", "Garlic Powder",
        "Garlic Sauce", "Gelatine Leafs", "Ghee", "Gherkin Relish", "Ginger",
        "Ginger Cordial", "Ginger Garlic Paste", "Ginger Paste", "Glace Cherry", "Goat Meat",
        "Goats Cheese", "Gochujang", "Golden Syrup", "Goose Fat", "Gouda Cheese",
        "Grand Marnier", "Granulated Sugar", "Grape Tomatoes", "Greek Yogurt", "Green Beans",
        "Green Chilli", "Green Olives", "Green Pepper", "Green Red Lentils", "Green Salsa",
        "Ground Almonds", "Ground Beef", "Ground Cumin", "Ground Ginger", "Ground Pork",
        "Gruyère", "Haddock", "Ham", "Hard Taco Shells", "Haricot Beans", "Harissa Spice",
        "Hazlenuts", "Heavy Cream", "Herring", "Honey", "Horseradish", "Hot Beef Stock",
        "Hotsauce", "Ice Cream", "Iceberg Lettuce", "Icing Sugar",
        "Italian Fennel Sausages", "Italian Seasoning", "Jalapeno", "Jam", "Jasmine Rice",
        "Jerk", "Jerusalem Artichokes", "Kale", "Khus Khus", "Kidney Beans", "Kielbasa",
        "King Prawns", "Kosher Salt", "Lamb", "Lamb Kidney", "Lamb Leg", "Lamb Loin Chops",
        "Lamb Mince", "Lard", "Lasagne Sheets", "Lean Minced Beef", "Leek", "Lemon",
        "Lemon Juice", "Lemon Zest", "Lemons", "Lentils", "Lettuce",
        "Light Brown Soft Sugar", "Light Rum", "Lime", "Linguine Pasta",
        "Little Gem Lettuce", "Macaroni", "Mackerel", "Madras Paste", "Malt Vinegar",
        "Maple Syrup", "Marjoram", "Mars Bar", "Marzipan", "Mascarpone",
        "Massaman Curry Paste", "Mayonnaise", "Medjool Dates", "Meringue Nests", "Milk",
        "Milk Chocolate", "Minced Beef", "Minced Garlic", "Minced Pork", "Mincemeat",
        "Miniature Marshmallows", "Mint", "Mirin", "Mixed Grain", "Mixed Peel",
        "Mixed Spice", "Monkfish", "Monterey Jack Cheese", "Mozzarella", "Mozzarella Balls",
        "Muffins", "Mulukhiyah", "Muscovado Sugar", "Mushrooms", "Mussels", "Mustard",
        "Mustard Powder", "Mustard Seeds", "Naan Bread", "Noodles", "Nutmeg", "Oatmeal",
        "Oats", "Oil", "Olive Oil", "Onion", "Onion Salt", "Onions", "Orange",
        "Orange Blossom Water", "Orange Zest", "Oregano", "Oxtail", "Oyster Sauce", "Oysters",
        "Paccheri Pasta", "Paella Rice", "Paneer", "Pappardelle Pasta", "Paprika",
        "Parma Ham", "Parmesan", "Parmesan Cheese", "Parmigiano-reggiano", "Parsley",
        "Passata", "Peaches", "Peanut Brittle", "Peanut Butter", "Peanut Cookies",
        "Peanut Oil", "Peanuts", "Pears", "Peas", "Pecan Nuts", "Pecorino", "Penne Rigate",
        "Pepper", "Persian Cucumber", "Pickle Juice", "Pilchards", "Pine Nuts",
        "Pink Food Colouring", "Pinto Beans", "Pita Bread", "Pitted Black Olives",
        "Plain Chocolate", "Plain Flour", "Plum Tomatoes", "Polish Sausage", "Pork",
        "Pork Chops", "Potato Starch", "Potatoe Buns", "Potatoes", "Powdered Sugar",
        "Prawns", "Pretzels", "Prosciutto", "Prunes", "Puff Pastry", "Pumpkin", "Quinoa",
        "Raisins", "Rapeseed Oil", "Ras el hanout", "Raspberries", "Raspberry Jam",
        "Raw King Prawns", "Red Chilli", "Red Chilli Flakes", "Red Chilli Powder",
        "Red Food Colouring", "Red Onions", "Red Pepper", "Red Pepper Flakes", "Red Snapper",
        "Red Wine", "Red Wine Jelly", "Redcurrants", "Refried Beans", "Rhubarb", "Rice",
        "Rice Krispies", "Rice Noodles", "Rice Stick Noodles", "Rice Vermicelli",
        "Rice Vinegar", "Rice wine", "Ricotta", "Rigatoni", "Roasted Vegetables", "Rocket",
        "Rolled Oats", "Rose water", "Rosemary", "Rum", "Saffron", "Sage", "Sake", "Salmon",
        "Salsa", "Salt", "Salt Cod", "Salted Butter", "Sardines", "Sauerkraut", "Sausages",
        "Scallions", "Scotch Bonnet", "Sea Salt", "Self-raising Flour", "Semi-skimmed Milk",
        "Sesame Seed", "Sesame Seed Burger Buns", "Sesame Seed Oil", "Shallots", "Sherry",
        "Shiitake Mushrooms", "Shortcrust Pastry", "Shortening", "Shredded Mexican Cheese",
        "Shredded Monterey Jack Cheese", "Sichuan Pepper", "Single Cream", "Small Potatoes",
        "Smoked Haddock", "Smoked Paprika", "Smoked Salmon", "Smoky Paprika", "Sour Cream",
        "Soy Sauce", "Soya Milk", "Spaghetti", "Spinach", "Spring Onions", "Squash", "Squid",
        "Sriracha", "Star Anise", "Starch", "Stilton Cheese", "Stir-fry Vegetables", "Stout",
        "Strawberries", "Suet", "Sugar", "Sugar Snap Peas", "Sultanas", "Sun-Dried Tomatoes",
        "Sunflower Oil", "Sushi Rice", "Swede", "Sweet Potatoes", "Sweetcorn", "Tabasco Sauce",
        "Tagliatelle", "Tahini", "Tamarind Ball", "Tamarind Paste", "Tarragon Leaves",
        "Thai Fish Sauce", "Thai Green Curry Paste", "Thai Red Curry Paste", "Thyme",
        "Tiger Prawns", "Tinned Tomatos", "Toffee Popcorn", "Tofu", "Tomato",
        "Tomato Ketchup", "Tomato Puree", "Tomato Sauce", "Tomatoes", "Toor Dal", "Tortillas",
        "Treacle", "Tripe", "Truffle Oil", "Tuna", "Turkey Mince", "Turmeric",
        "Turmeric Powder", "Turnips", "Udon Noodles", "Unsalted Butter", "Vanilla",
        "Vanilla Extract", "Veal", "Vegan Butter", "Vegetable Oil", "Vegetable Stock",
        "Vegetable Stock Cube", "Vermicelli Pasta", "Vine Leaves", "Vine Tomatoes", "Vinegar",
        "Vinaigrette Dressing", "Walnuts", "Warm Water", "Water", "Water Chestnut",
        "White Chocolate", "White Chocolate Chips", "White Fish", "White Fish Fillets",
        "White Flour", "White Vinegar", "White Wine", "White Wine Vinegar", "Whole Milk",
        "Whole Wheat", "Wholegrain Bread", "Wild Mushrooms", "Wonton Skin",
        "Wood Ear Mushrooms", "Worcestershire Sauce", "Yeast", "Yellow Food Colouring",
        "Yellow Onion", "Yellow Pepper", "Yogurt", "Yukon Gold Potatoes", "Zucchini"
    )

    // Ingredients to feature at the top
    val priorityIngredients = listOf("Beef", "Butter", "Garlic")

    // Create the final sorted list with priority items at the top
    val allIngredients = remember(jsonIngredients, priorityIngredients) {
        val uniqueJsonIngredients = jsonIngredients.toSet() // Ensure uniqueness from JSON
        val remainingIngredients = uniqueJsonIngredients
            .filterNot { it in priorityIngredients }
            .sorted()
        priorityIngredients + remainingIngredients
    }

    var query by remember { mutableStateOf("") }
    val filtered = remember(query, allIngredients) {
        if (query.isBlank()) {
            allIngredients
        } else {
            allIngredients.filter {
                it.contains(query.trim(), ignoreCase = true)
            }
        }
    }

    val selected = remember { mutableStateListOf<String>() }

    val pageTitle = when (mode) {
        IngredientSelectionMode.SEARCH_FILTER -> "Select Ingredients"
        IngredientSelectionMode.AUTO_GENERATE_PLAN -> "Generate by Ingredients"
    }
    val buttonText = when (mode) {
        IngredientSelectionMode.SEARCH_FILTER -> "Search by ${selected.size} Ingredient(s)"
        IngredientSelectionMode.AUTO_GENERATE_PLAN -> "Generate by ${selected.size} Ingredient(s)"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pageTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search Ingredients") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                LazyColumn(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filtered) { ingredient ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = selected.contains(ingredient),
                                onCheckedChange = { checked ->
                                    if (checked) selected.add(ingredient)
                                    else selected.remove(ingredient)
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(ingredient, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                Button(
                    onClick = { onSearch(selected.toList()) },
                    enabled = selected.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(buttonText)
                }
            }
        }
    )
}