<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="2024.10.18" name="Special Tiles" tilewidth="64" tileheight="64" tilecount="16" columns="4">
 <image source="../Tilesets/Special Tiles.png" width="256" height="256"/>
 <tile id="0">
  <properties>
   <property name="Related Object" value="QuestionBlock"/>
   <property name="State" type="int" value="0"/>
  </properties>
 </tile>
 <tile id="1">
  <properties>
   <property name="Related Object" value="QuestionBlock"/>
   <property name="State" type="int" value="1"/>
  </properties>
 </tile>
 <tile id="2">
  <properties>
   <property name="Local ID" type="int" value="2"/>
  </properties>
 </tile>
 <tile id="4">
  <properties>
   <property name="HEIGHT" type="int" value="64"/>
   <property name="Related Object" value="Player"/>
   <property name="State" type="int" value="0"/>
   <property name="WIDTH" type="int" value="48"/>
  </properties>
 </tile>
 <tile id="5">
  <properties>
   <property name="HEIGHT" type="int" value="128"/>
   <property name="Related Object" value="Player"/>
   <property name="State" type="int" value="1"/>
   <property name="WIDTH" type="int" value="48"/>
   <property name="note to self" value="Bottom tile"/>
  </properties>
 </tile>
 <tile id="6">
  <properties>
   <property name="Related Object" value="Player"/>
   <property name="State" type="int" value="2"/>
   <property name="note to self" value="Top tile"/>
  </properties>
 </tile>
</tileset>
