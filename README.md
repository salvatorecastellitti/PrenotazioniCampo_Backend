# PrenotazioniCampo_Backend
Backend for reservation sport's field

1. Pull dell'immagine di mysql da docker hub</br>
  docker pull mysql:5.7</br>
  [se ti da errore per architettura arm] docker pull --platform linux/x86_64 mysql:5.7</br>

2. Creare una docker network per la comunicazione tra spring e mysql</br>
  docker network create prenotazioni-pt</br>
  
3. Runnare il container mysql</br>
  docker run --name mysqldb --network prenotazioni-pt -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=prenotazioni -e MYSQL_USER=sa -e MYSQL_PASSWORD=1234 -d mysql:5.7</br>

4. [opzionale] controllare il database se è creato</br>
  docker exec -it "container-id" bash</br>
  mysql -usa -p1234</br>
  show databases;</br>
  
4.5 Inserire i ruoli nel db</br>
  use prenotazioni;</br>
  INSERT INTO roles(name) VALUES('ROLE_USER');</br>
  INSERT INTO roles(name) VALUES('ROLE_MODERATOR');</br>
  INSERT INTO roles(name) VALUES('ROLE_ADMIN');</br>

5. Buildare il progetto BE, cosi avrai il pacchetto .jar (io uso intellij tu non lo so 😱 )</br>

6. Buildare il docker file per avere l'immagine del BE\</br>
  [NB devi andare dentro alla cartella del progetto, al livello del dockerfile]</br>
  docker build -t prenotazioni-pt . </br>
  
7. Creare il volume per salvare le immagini:</br>
  docker volume create testSpring</br>
  
9. Avviare il container sullo stesso network del db e sul volume creato per le immagini</br>
  docker run --network prenotazioni-pt --name prenotazioniBE --mount source=testSpring,target=/etc/testSpring -p 8080:8080 -d prenotazioni-pt
</br>

