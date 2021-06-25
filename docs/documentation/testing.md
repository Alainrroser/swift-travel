# Swift-Travel

## Testing

> Version 1.0.0, 23. Juni 2021 | Alain Roser, Jan Henke

## Inhalt

1. [Testkonzept](#1-testkonzept)
    1. [Testumgebung](#11-testumgebung)
    2. [Testfälle](#12-testfälle)
2. [Testprotokolle](#2-testprotokolle)
    1. [Testperson](#21-Name-Testperson)


## 1 Testkonzept

### 1.1 Testumgebung

Software            |    Version
--------------------|-----------------------
Windows 10          |    1909
Android 10          |    Q x86
Nexus 5X            |    API 29

### 1.2 Testfälle

#### 1.2.1 Testbezeichnung

Abschnitt           |   Inhalt
--------------------|-----------------------
ID                  |   ST-01
Anforderungen       |   #01, #02
Vorbedingung        |   \-
Ablauf              |   Appstart <br> Floatingactionbuttonklick <br> Namenseingabe <br> Bildauswahl <br> Createklick
Erwartetes Resultat |	Die erstellte Reise wird auf dem Reisenbildschirm angezeigt

Abschnitt           |   Inhalt
--------------------|-----------------------
ID                  |   ST-02
Anforderungen       |   #03, #05
Vorbedingung        |   ST-01
Ablauf              |   Reiseklick <br> Floatingactionbuttonklick <br> Landauswahl
Erwartetes Resultat |	Das ausgewählte Land wird auf dem Reisedetailsbildschirm angezeigt

Abschnitt           |   Inhalt
--------------------|-----------------------
ID                  |   ST-03
Anforderungen       |   #04, #06
Vorbedingung        |   ST-02
Ablauf              |   Landklick <br> Floatingactionbuttonklick <br> Namenseingabe <br> Bildauswahl <br> Zeitspannenauswahl <br> Createklick
Erwartetes Resultat |	Die erstellte Stadt wird auf dem Landdetailsbildschirm angezeigt

Abschnitt           |   Inhalt
--------------------|-----------------------
ID                  |   ST-04
Anforderungen       |   #08, #09
Vorbedingung        |   ST-03
Ablauf              |   Stadtklick
Erwartetes Resultat |	In der erstellten Stadt wird eine Liste mit Tagen angezeigt, wessen Länge der Länge des Aufenthaltes entspricht

Abschnitt           |   Inhalt
--------------------|-----------------------
ID                  |   ST-05
Anforderungen       |   #11, #12
Vorbedingung        |   ST-03
Ablauf              |   Tagklick <br> Floatingactionbuttonklick <br> Namenseingabe <br> Createklick
Erwartetes Resultat |	Der erstellte Ort wird auf dem Tagdetailsbildschirm angezeigt

Abschnitt           |   Inhalt
--------------------|-----------------------
ID                  |   ST-05
Anforderungen       |   #07, #10, #13, #22
Vorbedingung        |   ST-01
Ablauf              |   Bearbeitungssymbolklick <br> Namenseingabe <br> Beschreibungseingabe <br> Submitklick
Erwartetes Resultat |	Der eingegebene Name und die eingegebene Beschreibung werden angezeigt

Abschnitt           |   Inhalt
--------------------|-----------------------
ID                  |   ST-06
Anforderungen       |   #21
Vorbedingung        |   ST-01
Ablauf              |   Zurücksymbolklick <br> Löschsymbolklick
Erwartetes Resultat |	Die Stadt, neben der das Löschsymbol geklickt wurde, wurde gelöscht

Abschnitt           |   Inhalt
--------------------|-----------------------
ID                  |   ST-07
Anforderungen       |   #14
Vorbedingung        |   ST-01
Ablauf              |   Zurücksymbolklick <br> Floatingactionbuttonklick <br> Suchsymbolklick <br> Namenseingabe
Erwartetes Resultat |	Es werden in der Liste nur Länder angezeigt, welche zu dem eingegebenen Text passen

Abschnitt           |   Inhalt
--------------------|-----------------------
ID                  |   ST-08
Anforderungen       |   #24
Vorbedingung        |   ST-01
Ablauf              |   Appschliessen <br> Appstart
Erwartetes Resultat |	Alle vor dem Schliessen hinzugefügte Informationen werden immer noch angezeigt, da sie in einer Datenbank gespeichert wurden 

Abschnitt           |   Inhalt
--------------------|-----------------------
ID                  |   ST-09
Anforderungen       |   #23
Vorbedingung        |   ST-01
Ablauf              |   Floatingactionbuttonklick <br> Namen leer lassen <br> Submitklick <br> Namenseingabe <br> Submitklick
Erwartetes Resultat |	Die Reise wird nicht hinzugefügt und es wird eine Fehlermeldung angezeigt, dass ein Name eingegeben werden muss, wenn man aber einen eingibt, wird sie hinzugefügt

Abschnitt           |   Inhalt
--------------------|-----------------------
ID                  |   ST-10
Anforderungen       |   #23
Vorbedingung        |   ST-03
Ablauf              |   Reiseklick <br> Landklick <br> Floatingactionbuttonklick <br> Namenseingabe <br> Zeitspannenauswahl leer lassen <br> Submitklick <br> Zeitspannenauswahl welche mit bereits existierender Stadt überlappt<br> Submitklick
Erwartetes Resultat |	Die Stadt wird nicht hinzugefügt und es wird eine Fehlermeldung angezeigt, dass eine Zeitspanne ausgewählt werden muss, ebenfalls wenn man aber eine auswählt, welche mit einer bereits existierenden Stadt überlappt

## 2 Testprotokolle

### 2.1 Vijayanantha Piriyan

> Datum, Zeit: 25.06.2021, 11.00
> Eingesetzte Software: Android 10, Swift-Travel, Nexus 5X
> Alle Testfälle positiv ohne Bemerkungen