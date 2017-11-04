# Outcome/Income Tracker

This is a my first program,
it's a basic program which tracks your outcomes and incomes.

I've created this in Eclipse using JavaFX.

versions
- 1.0
- 1.01	added ReadMe file
- 1.02	added methods descriptions
- 1.03	pre-sorting by date
- 1.04	added filtering entries (last 30 days) by ToggleButton there is a BUG entries cannot be sorted after filtering (it's not supported yet by FilteredList) || redesigned ToolBar || added Buttons for adding, editing and deleting || added arrow to the sorting column at the start of application || redesigned MenuBar
- 1.05	improved filtering (resign of using FilteredList) - now it supports sorting after filtering
- 1.06	further improvements to filtering (now it should work as intended - modifying data, when filtering is active, is now supported)
added statistics
- 1.07	binary format of storing data instead of text file
further minor changes
- 1.08	binary format with Serialization
- 1.09	data stored in singleton
- 1.10	better exceptions handling
- 1.11	open new data (adding to or replacing existing data) and save as
- 1.12	showing save data prompt on closing
- 1.13	lambda expressions on event handlers

Introduction data stored in database (using sqlite)
- 1.14d	adding libraries and creating database
- 1.15d	database table constants 
		
	

Still to implement:
- cell colors
