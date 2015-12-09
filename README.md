# q-report
Q&A communication tool for players and admins. Allows user to sends in game tickets.

#For End Users
Note: currently only 1.7.10 version of the game supported

1. Install Forgelin mod for 1.7.10 (you can get it [here](https://github.com/FRedEnergy/Forgelin/releases/tag/v0.1.0)
2. Download q-report mod. There are different versions available:
  * [qreport-client](https://github.com/FRedEnergy/q-report/releases/download/v1.0/qreport-v1.0-client.jar) - lightweight version for client, without unnecessary server dependencies. Won't work in single player
  * [qreport-server](https://github.com/FRedEnergy/q-report/releases/download/v1.0/qreport-v1.0-server.jar) - server side version, without client part
  * [qreport-standalone](https://github.com/FRedEnergy/q-report/releases/download/v1.0/qreport-v1.0-standalone.jar) - full version, it is not recommended to put on the server
3. If installing mod on server you also must download bukkit plugin [Vault](http://dev.bukkit.org/bukkit-plugins/vault/files/47-vault-1-4-1/)
4. Do not forget to configure mod (config usually can be found in `<game or server directory>/config/qreport-server.cfg`
  

#For Developers
##Clonning & Building
1. Clone repository using https://github.com/FRedEnergy/q-report.git
2. Navigate to the new folder `q-report`
3. Install forge development workspace using `gradlew setupDecompWorkspace`
4. Depending on your IDE:
  * For IntelliJ IDEA: run `gradlew idea` and import project as Gradle project
  * For Eclipse: run `gradlew eclipse` and open workspace at `/q-report/eclipse`
5. In order to build project use `gradlew build` and navigate to `q-report/build/libs` for jar files

# License
QReport is (C) 2015 RedEnergy and licensed under MIT License. Look throught LICENSE.md file for more information
