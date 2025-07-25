a)  `db.buch.insertOne({...})`
    `db.leser.insertOne({...})`
    `db.buch.inserMany([{...}, ...])`
    `db.leser.insertMany([{...}, ...])`

```
    db.entliehen.insertOne({
...     "LNR" : 36517836,
...     "INVNR" : 9863114,
...     "RÜCKGABEDATUM" : "2023-10-31"
... })
```

b)  `db.buch.findOne({"AUTOR": "Marc-Uwe Kling"})`
``` JSON 
{
  _id: ObjectId('68529fa14d03d83fc069e32e'),
  INVNR: '9783548609420',
  AUTOR: 'Marc-Uwe Kling',
  TITEL: 'Die Känguru-Chroniken: Ansichten eines vorlauten Beuteltiers',
  VERLAG: 'Ullstein-Verlag'
}
```
c)  `db.buch.countDocuments()`
7

d)  `db.entliehen.aggregate(...)`
``` JSON
[
    {
    $group: {
      _id: "$LNR",
      anzahlAusleihen: { $sum: 1 }
    }
  },
  {
    $match: {
      anzahlAusleihen: { $gt: 1 }
    }
  },
  {
    $sort: {
      anzahlAusleihen: -1
    }
  }
]
```

e)
``` js
const leserFF = db.leser.findOne({"NAME": "Friedrich Funke"})

const buchId = db.buch.findOne({"TITEL": "Die Känguru-Chroniken: Ansichten eines vorlauten Beuteltiers"}).INVNR

db.entliehen.insertOne(
    {
        "LNR": leserFF.LNR,
        "INVNR": buchId,
        "RÜCKGABEDATUM": "2026-03-04"
    }
)

db.entliehen.deleteOne(
    {
        "LNR": leserFF.LNR,
        "INVNR": buchId
    }
)
```

f)
``` js

db.buch.insertOne(
    {
        INVNR: 98666293,
        AUTOR: "Horst Evers",
        TITEL: "Der König von Berlin",
        VERLAG: "Rowolt-Verlag"
    }
)

db.leser.insertOne(
    {
        LNR: 9237279374,
        NAME: "Heinz Müller",
        ADRESSE: "Klopstockweg 17, 38124 Braunschweig",
        ENTLIEHEN: [
            {
                INVNR: buchId,
                RÜCKGABEDATUM: "2022-08-04" 
            },
            {
                INVNR: 98666293,
                RÜCKGABEDATUM: "2024-09-09"
            }
        ]
    }
)

```
Vorteil: Man spart sich die LNR und hat direkt alles beim Leser
Nachteil: Um zu schauen, ob ein bestimmtes Buch ausgeliehen ist muss jeder Nutzer durchgegangen werden


g)
``` js

const ausleihe = Array.from(db.leser.findOne({LNR: 9237279374}).ENTLIEHEN).filter((o) => o.INVNR !== "9783548609420");

db.leser.updateOne(
    {
        LNR: 9237279374
    },
    {
        "$set": {
            "ENTLIEHEN": ausleihe
        }
    }
)

db.leser.updateOne(
    {
        LNR: 3781678045610
    },
    {
        "$set": {
            "ENTLIEHEN": {
                INVNR: 9783548609420,
                RÜCKGABEDATUM : "2023-10-31"
            }
        }
    }
)
```

