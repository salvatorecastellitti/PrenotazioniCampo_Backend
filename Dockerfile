FROM openjdk:11

EXPOSE 8080

COPY target/PrenotazioniCampo_Backend-0.0.1-SNAPSHOT.jar PrenotazioniCampo_Backend-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/PrenotazioniCampo_Backend-0.0.1-SNAPSHOT.jar"]


