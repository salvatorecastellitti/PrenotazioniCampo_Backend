# PrenotazioniCampo_Backend
Backend for reservation sport's field

Per l'asino di Alex, segui le istruzioni per dockerizzare il back end.

1. Pull dell'immagine di mysql da docker hub\n
  docker pull mysql:5.7\n
  [se ti da errore per architettura arm] docker pull --platform linux/x86_64 mysql:5.7\n

2. Creare una docker network per la comunicazione tra spring e mysql\n
  docker network create prenotazioni-pt\n
  
3. Runnare il container mysql\n
  docker run --name mysqldb --network prenotazioni-pt -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=prenotazioni -e MYSQL_USER=sa -e MYSQL_PASSWORD=1234 -d mysql:5.7\n

4. [opzionale] controllare il database se è creato\n
  docker exec -it <container-id> bash\n
  mysql -usa -p1234\n
  show databases;\n
  
4.5 Inserire i ruoli nel db\n
  use prenotazioni;\n
  INSERT INTO roles(name) VALUES('ROLE_USER');\n
  INSERT INTO roles(name) VALUES('ROLE_MODERATOR');\n
  INSERT INTO roles(name) VALUES('ROLE_ADMIN');\n

5. Buildare il progetto BE, cosi avrai il pacchetto .jar (io uso intellij tu non lo so 😱 )\n

6. Buildare il docker file per avere l'immagine del BE\n
  [NB devi andare dentro alla cartella del progetto, al livello del dockerfile]\n
  docker build -t prenotazioni-pt . \n
  
7. Avviare il container sullo stesso network del db\n
  docker run --network prenotazioni-pt --name prenotazioniBE -p 8080:8080 -d prenotazioni-pt\n

🥳 Congratulazioni, ora puoi fare quello che vuoi con le api, sempre chiamandole da http://localhost:8080/ 🥳\n

Per qualsiasi dubbio o altro, contattarmi preferibilmente quando non dormo\n
