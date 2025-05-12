Possible assets:
- https://humblepixel.itch.io/pocket-inventory-series-10-monitor
- https://humblepixel.itch.io/pocket-inventory-series-1-adventure-book
- https://humblepixel.itch.io/pocket-inventory-series-5-player-status
- Commission tofupixel for background art
- https://kenney.nl/assets/category:2D?sort=update
- https://seliel-the-shaper.itch.io/fishing-gear
- https://cyangmou.itch.io/pixel-rpg-icons-fishing-16x16
- https://ansimuz.itch.io/underwater-diving

Useful stuff:
- https://mana-break.blogspot.com/2014/06/power-of-json-in-game-development-items.html

Quellen: 
- https://www.fishbase.se/search.php
- Fish cam niderlande ref
- libgdx

Layout:
1. Main Menu
   - Start Game
   - (Options)
   - Exit
   - Credits
2. Game
   - Computer screen with files for each room. you click on room to select level 
   - => Computer screen with party room number, guest list, fish camera and buttons to accept or deny fish
   - Press J to open Journal
   - You have 3 lives, each time you accept a fish that doesn't belong in the party, you lose a life (maybe also if you deny a fish that belongs in the party)
   - When all lives are lost, the party guests leave and you have to start over
   - If you have completed a level you go back to the computer screen and can select the next party room to protect
   - After completing a level, all fishes that were accepted (no matter if correctly or not) will have more details in the journal
3. Guest List
   - All features the fishes need to have in order to be accepted

---

In the Fish Fiesta game, you are the gatekeeper of several fish parties. You receive a list of conditions that guests must meet to enter the party room. A small window displays a fish, and you must check your journal to verify if the fish meets the guest list conditions. You can then choose to either accept or decline the fish. 

You have 3 lives. If you decline a fish that should be allowed into the party or accept a fish that shouldn't, you lose a life. A level represents a fish party, each with a set guest list and a list of fish IDs attempting to enter. 

In the level selection screen, you can see which levels are completed and which ones can be played. This screen also displays how many hearts the player lost in each level. However, to optimize performance, only the level ID, completion status, and lives lost are loaded here. The full level data (e.g., guest list) is loaded only when a level is selected.

Within a level, you can view the guest list conditions and start the shift. When the shift begins, the first fish ID from the list is used to load the first fish. Fish textures are loaded using their IDs, which are mapped to skins in an atlas. This allows for easy retrieval of drawable textures.

Fishes are represented by the `WaterCreature` class. The game should allow saving the state of a level so players can resume later. Level progress should also be displayed in the level selection screen. Levels are defined via JSON, while fishes can be registered in code.

---

## Room Conditions
A room can only have one condition per category (most of the time). Meaning a room _can not_ be warm and cold or deepsea and coast. It _can_ be warm and deepsea tho. (even though this wouldn't make much sense)

1. Temperature
   - Warm
   - Cold
   - Medium
2. Water Type
   - Salt
   - Fresh
3. Water-subtype
   - Deepsea
   - Coast
   - Open Ocean
   - Coral Reef
   - Lake
   - River
   - Kelp Forrest
4. Served Food
   - TODO later
5. Size
   - Big
   - Medium
   - Small
6. Lifespan
   - TODO Later
7. Eating habbit (?)
8. Schooling

## Fish Features
The fish has the same conditions as represented above. However here they represent fish features/behaviors.
Additionally, fish can have multiple features from one category (sometimes). Meaning they _can_ be salt and freshwater fish.
