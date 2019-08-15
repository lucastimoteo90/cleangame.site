var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

var clients = {};
var users = [];

app.get('/', function(req, res){
  res.send('server is running');
});

http.listen(2000, function(){
   console.log('listening on port 2000');
});

io.on("connection", function (client) {
    console.log('user connected');
      client.on("register_user", function(user){
        console.log("Usuário registrado: ", user.mail);       
        //client.emit("update", "You have connected to the server.");
        //client.broadcast.emit("update", name + " has joined the server.")
      });
      
      
      client.on("register_user_in_battle", function(battle){
          console.log("Usuário registrado na batalha: "+ battle.id);                                  
          client.join(battle.id);
          client.in(battle.id).emit("new_user_in_room_battle", battle.id);                              
          //client.broadcast.emit("update", name + " has joined the server.")
      });
      
      client.on("join_battle", function(battle){
          console.log("SEND JOIN BATTLE: ",battle.id);       
          client.in(battle.id).emit("join_battle", battle);
          //client.broadcast.emit("update", name + " has joined the server.")
      });  
      
      
      client.on("register_user_in_room", function(room){
          console.log("Usuário registrado na sala: ", room.usermail);                                  
          client.join(room.id);
          client.in(room.id).emit("new_user_in_room", room.usermail);                              
          //client.broadcast.emit("update", name + " has joined the server.")
      });
      
    
      
      client.on("makealternative", function(room){
          console.log("Make alternative: ");       
          client.in(room).emit("makealternative", "");
          //client.broadcast.emit("update", name + " has joined the server.")
      });
      
      client.on("gettip", function(dica){
          console.log("GET TIP: ",dica);       
          client.in(dica.id).emit("gettip", dica);
          //client.broadcast.emit("update", name + " has joined the server.")
      });
      
      client.on("sendchatmsg", function(mensagem){
          console.log("SEND CHAT MENSAGE: ",mensagem);       
          client.in(mensagem.sala).emit("sendchatmsg", mensagem);
          //client.broadcast.emit("update", name + " has joined the server.")
      });  
      
      
     
      
      
      client.on("movemouse",function(moviment){
        // console.log("Movimento Mouse: ",moviment);
         client.in(moviment.room).emit("movereceiver", moviment);
      })
      
    
      client.on("join", function(name){
        console.log("Joined: " + name);
        clients[client.id] = name;
        client.emit("update", "You have connected to the server.");
        client.broadcast.emit("update", name + " has joined the server.")
      });
      
      client.on("entrasala", function(sala){
          console.log("Entrou na sala: " + sala);
          //clients[client.id] = name;
          client.join(sala);
          client.in(data.sala).emit('newuserroom', data.data);
          
          //client.broadcast.emit("update", name + " has joined the server.")
       });
      
      client.on("msgsala", function(data){
    	  data = JSON.parse(data);
          console.log("MSG SALA: " + data.sala);
          //clients[client.id] = name;
         // client.join(sala);
          client.in(data.sala).emit('msgsala', data.data);
          //client.broadcast.emit("update", name + " has joined the server.")
       });
      

      client.on("send", function(msg){
        console.log("Message: " + msg);
        client.broadcast.emit("chat", clients[client.id], msg);
      });

      client.on("disconnect", function(){
        console.log("Disconnect");
        io.emit("update", clients[client.id] + " has left the server.");
        delete clients[client.id];
      });
      
      
});