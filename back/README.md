# Backend - Your Car Your Way - POC

Ce projet est une POC.
Objectif : permettre à un utilisateur et au support d’échanger des messages simplement et en toute sécurité.
L’app utilise WebSocket + STOMP pour pousser les mises à jour en temps réel (création de chat, nouveaux messages, changement de statut).

## **Prérequis**

- **Java 17**
- **Maven** pas forcément obligatoire car il est souvent intégré par les IDE mais peut être utile
- **Docker Desktop** installé et configuré.

## **Installation et Exécution**

### **Étape 1 : Cloner le projet**

```bash
git clone 
cd back
```

### **Étape 2 : Générer une clé secrète (optionnel si vous utilisez la clé par défaut)**

Avant de démarrer le projet, vous devez générer une clé secrète (pour la partie JWT).

Afin de créer la clé voici les étapes :

1. Ouvrez un terminal PowerShell.
2. Exécutez la commande suivante:

```powershell
    $bytes = New-Object byte[] 64
    [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
    [BitConverter]::ToString($bytes).Replace("-", "")
```

3. Copiez la clé générée.
4. Ajoutez-la dans le fichier `src/main/resources/application.properties` en remplacant jwtsecret:

```properties
jwt.secret=jwtsecret
```

### **Étape 3 : Configuration de la base de données avec Docker**

1. Exécutez la commande suivante pour démarrer un conteneur MySQL avec Docker (springbootdb et springuser et secretpassword et rootpassword sont bien évidemment des valeurs par défault):

```bash
docker run -d --name springboot-mysql -e MYSQL_DATABASE=springbootdb -e MYSQL_USER=springuser -e MYSQL_PASSWORD=secretpassword -e MYSQL_ROOT_PASSWORD=rootpassword -p 3306:3306 mysql:latest
```

2. Ouvrez le fichier `src/main/resources/application.properties`.
3. Assurez-vous que les configurations MySQL correspondent aux paramètres du conteneur :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/springbootdb
spring.datasource.username=springuser
spring.datasource.password=secretpassword
```

4. (optionnel) Vous pouvez load le fichier `../bdd/springbootdb.sql` dans votre base de données pour avoir des données de tests.
- Utilisateur support:
  - email: test@test.fr
  - password: test
- Utilisateur client:
  - email: aze@aze.fr
  - password: aze

### **Étape 4 : Démarrer le backend**

Vous pouvez utiliser plusieurs IDE pour développer et exécuter le projet. **IntelliJ IDEA** simple et efficace.

1. **Effectuer un `clean` et un `install` avec Maven** :

   - Ouvrez un terminal dans l'IDE.
   - Exécutez la commande suivante:

   ```bash
   mvn clean install
   ```

### **Étape 5 : Accéder à la documentation swagger de l'API (optionnel)**

Une fois le projet lancé, vous pouvez accéder au Swagger à l'adresse suivante :

http://localhost:3001/swagger-ui/index.html

### **Étape 6 : Accéder au endpoint STOMP**

Une fois le projet lancé, vous pouvez accéder au ws à SockJS à l'adresse suivante :
http://localhost:3001/ws