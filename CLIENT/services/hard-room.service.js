app.service('$HardRoomService', ['$http', 'ApiPath','$TeamService', function ($http, ApiPath, $TeamService) {

  
    //Mantem dados do usuario autenticado
    this.room = {}
    
    this.setActiveRoom = function(room){
        this.room = room;
    }

    this.getActiveRoom = function(room){
       return this.room;
    }

    this.getListBadsmells = function () {
        /**
         * Configura o cabeçalho
         */
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }

        return $http.get(ApiPath + '/badsmell/', config).then(function (response) {
            console.log("lista de badsmells: "+response);
            return response;                         
        }).catch(function (err) {
            console.log("ERRO: Falha ao obter questão...",err)
            return err;
        });
    }


    this.getQuestions = function () {
        /**
         * Configura o cabeçalho
         */
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }

        return $http.get(ApiPath + '/hardroom/'+this.getActiveRoom().id+'/questions/', config).then(function (response) {
            return response;                         
        }).catch(function (err) {
            console.log("ERRO: Falha ao obter questão...",err)
            return err;
        });
    }

    this.getQuestion = function (battle_id) {
        /**
         * Configura o cabeçalho
         */
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }

        console.log("TEAM SERVICE",$TeamService);

        return $http.get(ApiPath + '/hardroom/'+battle_id+'/'+this.getActiveRoom().id+'/question/'+$TeamService.getActiveTeam().id+"/", config).then(function (response) {
            return response;                         
        }).catch(function (err) {
            console.log("ERRO: Falha ao obter questão...",err)
            return err;
        });
    }

    this.insertNewRoom = function(room){
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }
        return $http.post(ApiPath + '/hardroom/', room, config).then(function(response) {
            console.log("room")                      
            return response
        }).catch(function (err) {
            console.log("ERRO: Falha ao criar sala...")
            return err;
        });
    }

    this.insertQuestion = function(question){
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }

        return $http.post(ApiPath + '/hardroom/'+this.getActiveRoom().id+'/question/', question, config).then(function(response) {
                  
             console.log("Question")                     
                                  
            return response
        }).catch(function (err) {
            console.log("ERRO: Falha ao criar sala...")
            return err;
        });
    }


    this.newBattle = function(team_id){
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }

        return $http.get(ApiPath + '/hardroom/new_battle/'+team_id,  config).then(function(response) {
                  
             console.log("LOAD BATTLE ID")                     
                                  
            return response
        }).catch(function (err) {
            console.log("ERRO: Falha ao obeter LOAD BATTLE ID")
            return err;
        });
    }
  
    this.joinBattle = function(battle_id,team_id){
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }

        return $http.get(ApiPath + '/hardroom/join_battle/'+battle_id+'/'+team_id,  config).then(function(response) {
                  
             console.log("LOAD BATTLE ID")                     
                                  
            return response
        }).catch(function (err) {
            console.log("ERRO: Falha ao obeter LOAD BATTLE ID")
            return err;
        });
    }

    this.getBattle = function(battle_id){
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }

        return $http.get(ApiPath + '/battle/'+battle_id,  config).then(function(response) {
                  
             console.log("LOAD BATTLE ID")                     
                                  
            return response
        }).catch(function (err) {
            console.log("ERRO: Falha ao obeter LOAD BATTLE ID")
            return err;
        });
    }

   
    
    this.delete = function(room){
        var config = {
            headers: {
                Authorization: localStorage.getItem("cleangameToken")
            }
        }
        return $http.post(ApiPath + '/rooms/delete/'+room.id,null,config).then(function(response) {
            console.log(response)                     
                                  
            return response
        }).catch(function (err) {
            console.log("ERRO: Falha ao efetuar login do usuário...",err)
            return err;
        });
    }
   
}])