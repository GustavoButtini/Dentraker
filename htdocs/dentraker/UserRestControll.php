<?php 
    require_once ("connection.php");
    require_once ("SimpleRest.php");
    class UserRestControll extends SimpleRest{
        
        function getAllUsers(){
            $con = new Connection();
            $data = $con->getAllEntries("usuarios","idUsu");
            if(empty($data)){
                $statuscode = 404;
                $data = array(
                    'Erro' => "Sem dados identificados"
                );
            }else{
                $statuscode = 200;
            }
            $contenttype = @$_SERVER['HTTP_ACCEPT'];
            $this->setHttpHeaders($contenttype,$statuscode);
            if(strpos($contenttype,'application/json') !== false){
                $res = json_encode($data);
                echo $res;
            }
        }
        function getUserById($id){
            $con = new Connection();
            $user = $con->getEntryById("usuarios","idUsu",$id);
            if(empty($user)){
                $statuscode = 404;
                $user = array(
                    "Error" => "Not found data !"
                );
            }else{
                $statuscode = 200;
            }
            $contenttype = @$_SERVER['HTTP_ACCEPT'];
            $this->setHttpHeaders($contenttype,$statuscode);
            if(!strpos($contenttype,'application/json')){
                echo (json_encode($user));
            }
        }
        function getUserLogin($user,$pass){
            $data = "";
            $con = new Connection();
            $users = $con->getEntriesByCustomSQL("SELECT * FROM usuarios WHERE username = '".$user."'");
            if(empty($users)){
                $statuscode = 404;
                $data = Array(
                    "Error" => "usernameinvalid"
                );
            }
            foreach($users as $usuario){
                if(password_verify($pass,$usuario["senha"])){
                    $data = $usuario;
                }
            }
            if($data == ""){
                $statuscode = 202;
                $data = Array(
                    "Error" => "wrongpass"
                );
            }else{
                $statuscode = 202;
            }
            $contenttype = @$_SERVER["HTTP_ACCEPT"];
            $this->setHttpHeaders($contenttype,$statuscode);
            if(strpos($contenttype,'application/json') !== false){
                echo (json_encode($data));
            }
        }
        function insertData($username,$pass,$nome,$phone,$mail,$bdate,$insp=null){
            $con = new Connection();
            $res = $con->setNewUser($username,$pass,$nome,$phone,$mail,$bdate,$insp);
            if(!$res){
                $statuscode = 404;
                $res = array(
                    "Error" => "User Not Inserted"
                );
            }else{
                $statuscode = 200;
            }
            $contenttype = @$_SERVER['HTTP_ACCEPT'];
            $this->setHttpHeaders($contenttype,$statuscode);
            echo (json_encode(array("Sucess" => "User Inserted")));
        }
        function getCommentatorsByCall($id){
            $con = new Connection();
            $data = $con->getEntriesByCustomSQL("SELECT comentarios.*, usuarios.nome as nome_usuario from comentarios  inner join usuarios on comentarios.idUsu = usuarios.idUsu where idChamado = ".$id."");
            $statuscode = 200;
            if(empty($data)){
                $data = array("NoData"=>"Data not found");
            }
            $contenttype = @$_SERVER['HTTP_ACCEPT'];
            $this->setHttpHeaders($contenttype,$statuscode);
            if(strpos($contenttype,'application/json') !== false){
                echo (json_encode($data));
            }
        }
    }
?>