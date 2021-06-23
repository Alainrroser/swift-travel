# Swift-Travel

## Inhalt

1. [Abstract (Kurzbeschreibung)](#1-abstract-kurzbeschreibung)
2. [Konkurrenzanalyse](#2-konkurrenzanalyse)
3. [Mockups](#4-mockups)
4. [Technische Realisierung](#5-technische-realisierung)
5. [Testing](#6-testing)
    1. [Manuelle UI-Tests](#61-manuelle-ui-tests)
    2. [Testauswertung](#62-testauswertung)
6. [Fazit](#7-fazit)

# Abstract (Kurzbeschreibung)

> Swift-Travel ist ein Projekt bei dem der Benutzer seine zukünftigen Reisen planen, sowie auch seine bereits getätigten Reisen festhalten und später wieder betrachten kann
> Man hat dafür Trips, welche man hinzufügen kann, worin man Länder, welche man auf diesem Trip besucht, hinzufügen kann
> Dort wiederum hat man die Möglichkeit Städte in den einzelnen Ländern hinzuzufügen
> Pro Stadt hat man dann Tage (Die Anzahl entspricht der Zeitspanne, welche man in dieser Stadt verbringt) in welchen man dann festhalten kann, was man hier gemacht hat

# Konkurrenzanalyse

## [TripIt: Reiseplaner](https://play.google.com/store/apps/details?id=com.tripit)
## [TripPlanner - Trips & Travel planner(no sign-in](https://play.google.com/store/apps/details?id=com.travalour.tripplanner)
## [My Travel Planner App](https://play.google.com/store/apps/details?id=com.travefy.travelplannersapp.tripplans)	
## [Roadtrippers - Trip Planner](https://play.google.com/store/apps/details?id=com.roadtrippers)	
> Was machen sie gut
	* Auflistung und Unterteilung der Reisen 
	* Übersichtlich
> Was machen sie schlecht 
	* Persönliche Erinnerungen können nicht hinzugefügt werden 
	* Beschränkt (Keine genaueren Informationen über Ausflüge)
> Wie können wir uns abheben
	* Persönlicher (Möglichkeit persönliche Erinnerungen hinzufügen zu können)
	* Weniger beschränkt (Möglichkeit Informationen über Ausflüge hinzufügen zu können)
	* Ziel ist nicht die Planung sondern die Reisen
## [Lambus | Reiseplaner](https://play.google.com/store/apps/details?id=io.lambus.app)
## [NextTripPlan | Trip Planner](https://play.google.com/store/apps/details?id=com.devpira.travel_plan)
## [Itinerate](https://play.google.com/store/apps/details?id=com.blahovici.itinerate)	
> Was machen sie gut
	* Persönliche Erinnerungen können hinzugefügt werden 
> Was machen sie schlecht 
	* Unübersichtlich
> Wie können wir uns abheben
	* Einfacher und übersichtlicher aufgebaut
	* Ziel ist nicht die Planung sondern die Reise

# Design
## 4.1 Mockups

> ***1. Trips***  
> [Trips](docs/images/Trips.png)
> Dies ist die Startactivity. Man hat hier die Möglichkeit mithilfe eines Floating Action Buttons Reisen hinzuzufügen, welche dann in einer Liste angezeigt werden.

> ***2.	Tripdetails***  
> ![Tripdetails](docs/images/TripDetails.png)
> In diese kommt man, indem man einen Eintrag der Liste der ersten Activity anklickt. Hier hat man zuerst Informationen zum Trip, welche man auch bearbeiten kann
> Darunter ist ebenfalls eine Liste, diesmal aber mit allen Ländern, welche man in diesem Trip besucht und auch hier hat es einen Floating Action Button, mitwelchem man Länder hinzufügen kann

> ***3. Sign In***   
> ![ChooseCountry](docs/images/ChooseCountry.png)
> In diese Activity kommt man durch den Klick auf den Floating Action der TripDetailsActivity klickt
> Sie beinhaltet eine Liste mit allen Ländern, welche man auch durchsuchen kann, welche man dann zu der Liste der Länder im Trip der TripDetailsActivity hinzufügen kann

## Klassendiagramm
![Klassendiagramm](docs/images/uml.png)

# Technische Realisierung

## Room Database
> Alle Trips, Countries, Cities und Days werden in lokal in einer Room Datenbank gespeichert

### SwiftTravelDatabase
> Die Datenbank mit den Gettern für die DAOs

### DAOs
> TripDao, CountryDao, CityDao und DayDao
> Die Data Access Objects mit den Abfragen für die Entities

### Entities 
> Trip, Country, City, Day 
> Die Entities mit den einzelnen Columns und den Gettern und Settern dafür

## Restcountries API
> Bei der TripDetailsActivity soll eine Liste von den Ländern angezeigt werden, welche man in diesem Trip besucht

### ChooseCountryActivity
> Man kann Countries hinzufügen mit einem Klick auf den Floating Action Button in den TripDetails hinzufügen
> Man kommt dann in diese Activity wo man eine Liste mit den Ländernamen und Flaggen sieht
> Diese Informationen werden von der restcountries API unter [diesem Link](https://restcountries.eu/rest/v2/all) abgerufen

# Testing

## Manuelle UI-Tests
> [Testing](testing.md)

# Fazit

> * *Was lief gut/schlecht?*
> * *Wie seid ihr mit dem Ergebniss zufrieden?*
> * *Was habt ihr gelernt?*
> * *War alles vorhanden oder was fehlte noch?*

## Positives 
>
## Negatives
>
