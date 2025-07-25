## FirmaDB in Mongo:
Die Datenbank muss **nicht** in einer Normalform stehen, da MongoDB eine nicht-relationale Datenbank ist.\
Auch müssen nicht alle Zusammenhänge zwischen Entitäten über fremdschlüsselbeziehungen repräsentiert werden, sondern können teilweise in die Dokumente direkt gespeichert werden. 

### Tabellen übertragen:
Existente Tabellen:
- abteilung
- gehalt
- kind
- maschine
- personal
- praemie

#### Abteilung:
Nur eine Zuordnung von Abteilungsnummer und -name. Kann gestrichen werden.
Das Personal bekommt direkt den Namen seiner Abteilung übergeben.

#### Gehalt:
Da das Gehalt in Gehaltsstufen unterteilt ist, ist es sinnvoll hierfür eine eigene Collection zu erstellen um ein leichtes Ändern zu gewährleisten.

#### Kind:
Da ein Mitarbeiter mehrere Kinder haben kann ist es sinnvoll für die Kinder eine neue Collection anzulegen, die mit einer pnr für den zugehörigen Mitarbeiter versehen ist.

#### Maschine:
Da zu einer Maschine viele wichtige Informationen zur Verfügung stehen ist es nicht sinnvoll alle davon in einem Mitarbeiter zu speichern. Folglich wird eine neue Collection angelegt.

#### Personal:
Personal ist eine der wichtigsten Tabellen in der relationalen Datenbank und bekommt eine eigene Collection.

#### Praemie:
Eine Prämie ist eine Liste von Beträgen, die einem Mitarbeiter zugeordnet sind. Diese kann in einen Mitarbeiter aufgelistet werden.


### Folgende Collections werden erstellt:

#### Gehalt:
- String stufe
- Int betrag

#### Kind:
- Int pnr ↑
- String name
- String Vorname
- String Geburtstag

#### Maschine:
- Int mnr
- String name
- Int pnr ↑
- String anschaffung
- Int neuwert
- Int zeitwert

#### Personal
- String abteilung
- Int[] praemien
- String gehalt ↑
- Int pnr
- String name
- String vorname
- String krankenkasse