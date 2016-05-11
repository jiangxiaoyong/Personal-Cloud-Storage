running app with docker
===============================
docker mysql
-----------------------------
- copy sql script to instance
- docker run --name mysql -v /var/tmp:/var/sql -e MYSQL_ROOT_PASSWORD=123 -d mysql

docker tomcat
--------------------------------
- docker run --name tomcat -it -p 8080:8080 --link mysql:mysql -v /var/app/current:/usr/local/tomcat/webapps tomcat:7-jre8

or with env variable
----------------------------
- docker run --name tomcat -it -p 8080:8080 --link mysql:mysql -e MYSQL_PORT_3306_TCP_ADDR='172.17.0.2' -e MYSQL_PORT_3306_TCP_PORT='3306' -v /home/jxy/IdeaProjects/Personal-Cloud-Storage/SpringMVC/target/:/usr/local/tomcat/webapps tomcat:7-jre8

tomcat default port change
----------------------------
- find server.xml and <Connector connectionTimeout="20000" port="8080" protocol="HTTP/1.1" redirectPort="8443"/> change port=8080 to port=80
