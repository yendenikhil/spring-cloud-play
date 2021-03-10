#! /bin/sh 

cd config-service 
./run.sh

cd ../discovery-service 
./run.sh 

cd ../id-service 
./run.sh

cd ../router-service
./run.sh

