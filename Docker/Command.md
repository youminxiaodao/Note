sudo docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=<szkd@@600446>" \
   -p 1433:1433 --name sql1 \
   -d mcr.microsoft.com/mssql/server:2017-latest





docker run –cap-add SYS_PTRACE -e ‘ACCEPT_EULA=1’ -e ‘MSSQL_SA_PASSWORD=szkd@@600446’ \
  -p 1433:1433 –name azuresqledge \
  -d mcr.microsoft.com/azure-sql-edge  



## 运行docker容器

sudo docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=szkd@@600446" \
   -p 1433:1433 --name azuresqledge \
   -d mcr.microsoft.com/azure-sql-edge

## 进入docker下某个容器控制台

sudo docker exec -it azuresqledge "bash"

## docker执行命令


sudo docker exec -it azuresqledge mkdir -p /var/opt/mssql/backup


sudo docker cp kbssoptrisk_1300_202104260101.bak azuresqledge:/home/mssql/


sudo docker exec -it azuresqledge restore filelistonly from disk='/var/opt/mssql/backup/kbssoptrisk_1300_202104260101.bak'






sudo docker cp kbssoptsetthisfar_1300_202104260101.bak azuresqledge:/home/mssql/

sudo docker cp kbssoptsetthis_1300_202104260101.bak azuresqledge:/home/mssql/

sudo docker cp kbssoptsett_1300_202104260101.BAK azuresqledge:/home/mssql/

sudo docker cp opt_stds_1300_202104260101.BAK azuresqledge:/home/mssql/