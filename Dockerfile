FROM openjdk:11

COPY target/PrenotazioniCampo_Backend-0.0.1-SNAPSHOT.jar PrenotazioniCampo_Backend-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/PrenotazioniCampo_Backend-0.0.1-SNAPSHOT.jar"]


