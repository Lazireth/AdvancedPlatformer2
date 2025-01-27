<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.1" name="NPC Tileset" tilewidth="64" tileheight="64" tilecount="16" columns="4">
 <image source="../Tilesets/NPC Tileset.png" width="256" height="256"/>
 <tile id="0">
  <properties>
   <property name="enemyType" value="Basic"/>
   <property name="relatedObject" value="BasicEnemy"/>
   <property name="state" type="int" value="0"/>
  </properties>
  <objectgroup draworder="index" id="3">
   <object id="4" x="8" y="8" width="48" height="48"/>
  </objectgroup>
 </tile>
 <tile id="1">
  <properties>
   <property name="enemyType" value="Basic"/>
   <property name="relatedObject" value="BasicEnemy"/>
   <property name="state" type="int" value="1"/>
  </properties>
 </tile>
</tileset>
