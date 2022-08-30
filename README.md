# PrenotazioniCampo_Backend
Backend for reservation sport's field

Per l'asino di Alex, segui le istruzioni per dockerizzare il back end.

1. Pull dell'immagine di mysql da docker hub
  docker pull mysql:5.7
  [se ti da errore per architettura arm] docker pull --platform linux/x86_64 mysql:5.7

2. Creare una docker network per la comunicazione tra spring e mysql
  docker network create prenotazioni-pt
  
3. Runnare il container mysql
  docker run --name mysqldb --network prenotazioni-pt -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=prenotazioni -e MYSQL_USER=sa -e MYSQL_PASSWORD=1234 -d mysql:5.7

4. [opzionale] controllare il database se è creato
  docker exec -it <container-id> bash
  mysql -usa -p1234
  show databases;

5. Buildare il progetto BE, cosi avrai il pacchetto .jar (io uso intellij tu non lo so 😱 )

6. Buildare il docker file per avere l'immagine del BE
  [NB devi andare dentro alla cartella del progetto, al livello del dockerfile]
  docker build -t prenotazioni-pt .
  
7. Avviare il container sullo stesso network del db
  docker run --network prenotazioni-pt --name prenotazioniBE -p 8080:8080 -d prenotazioni 

🥳 Congratulazioni, ora puoi fare quello che vuoi con le api, sempre chiamandole da http://localhost:8080/ 🥳

Per qualsiasi dubbio o altro, contattarmi preferibilmente quando non dormo
