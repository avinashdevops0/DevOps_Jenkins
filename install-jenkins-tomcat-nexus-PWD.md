### **Run Jenkins using Docker**
```bash
docker run -d \
  --name jenkins \
  --rm \
  -u root \
  -p 8080:8080 \
  -p 50000:50000 \
  -v /usr/bin/docker:/usr/bin/docker \
  -v /usr/local/bin/docker-compose:/usr/local/bin/docker-compose \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:latest
  ```

  ### ***Jenkins Installation on Alpine Linux***
  ```sh 
  # Update package index
apk update

# Add community repo if needed
echo "https://dl-cdn.alpinelinux.org/alpine/latest-stable/community" >> /etc/apk/repositories
apk update

# Install Java 21
apk add openjdk21

# Install Jenkins dependencies
apk add curl

# Install font libraries needed for Java AWT (charts in Jenkins)
apk add fontconfig ttf-dejavu

# Optional: additional common fonts
apk add ttf-droid ttf-freefont

# Download Jenkins WAR
curl -L -o jenkins.war https://get.jenkins.io/war-stable/latest/jenkins.war

# Run Jenkins
java -jar jenkins.war --httpPort=8080 &>> jenkins.log &

```

### ***Install Tomcat on Alpine Linux***
```sh
#!/bin/sh

# Install Java 17, wget, tar
apk update
apk add openjdk17 wget tar

# Set Tomcat version
TOMCAT_VERSION=9.0.112

# Download and extract Tomcat
cd /opt || exit 1
wget https://archive.apache.org/dist/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz
tar -zxvf apache-tomcat-${TOMCAT_VERSION}.tar.gz

# Configure Tomcat users
sed -i '56 a\<role rolename="manager-gui"/>' apache-tomcat-${TOMCAT_VERSION}/conf/tomcat-users.xml
sed -i '57 a\<role rolename="manager-script"/>' apache-tomcat-${TOMCAT_VERSION}/conf/tomcat-users.xml
sed -i '58 a\<user username="tomcat" password="admin@123" roles="manager-gui,manager-script"/>' apache-tomcat-${TOMCAT_VERSION}/conf/tomcat-users.xml
sed -i '59 a\</tomcat-users>' apache-tomcat-${TOMCAT_VERSION}/conf/tomcat-users.xml
sed -i '56d' apache-tomcat-${TOMCAT_VERSION}/conf/tomcat-users.xml

# Disable IP restriction for Manager app
sed -i '21d' apache-tomcat-${TOMCAT_VERSION}/webapps/manager/META-INF/context.xml
sed -i '22d' apache-tomcat-${TOMCAT_VERSION}/webapps/manager/META-INF/context.xml

# Set permissions and start Tomcat
chmod +x apache-tomcat-${TOMCAT_VERSION}/bin/*.sh
sh apache-tomcat-${TOMCAT_VERSION}/bin/startup.sh

echo "Tomcat ${TOMCAT_VERSION} started"
echo "User: tomcat"
echo "Password: admin@123"
echo "Port: 8080"

```


avinash