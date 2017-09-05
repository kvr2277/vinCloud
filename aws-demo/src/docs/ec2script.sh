#!/bin/bash
sudo su
yum update -y
yum -y install git
git clone https://github.com/kvr2277/vinzone.git
sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven
mkdir -p /var/www/html
cd /var/www/html
printf '%s\n' 'temp.directory=/var/www/html' 'topic.arn=<<REPLACE queue arn>>' >app.properties
find /var/www -type d -exec chmod 777 {} \;
cd /home/ec2-user/vinzone/aws-demo
mvn clean install
iptables --insert INPUT --protocol tcp --dport 80 --jump ACCEPT
iptables --insert INPUT --protocol tcp --dport 8080 --jump ACCEPT
iptables --table nat --append PREROUTING --in-interface eth0 --protocol tcp --dport 80 --jump REDIRECT --to-port 8080
# run next line to have changes survive reboot
service iptables save
java -jar /home/ec2-user/vinzone/aws-demo/target/aws-demo-1.0-SNAPSHOT.jar
