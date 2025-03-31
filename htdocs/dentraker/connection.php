<?php 
    class Connection{
        private $con;
        private $res;
        private function startCon(){
            $url = "localhost:3306";
            $user = "root";
            $pass = "Lukas8899!";
            $db = "dentraker";
            $this->con = new mysqli($url,$user,$pass,$db);
            if($this->con->connect_error){
                die("Banco De Dados não connectado");
            }
        }
        private function settingData(string $table, string $idslug,int $id=null){
            $this->startCon();
            $sql = ($id != null) ? "SELECT * FROM ".$table." WHERE ".$idslug."=".$id : "SELECT * FROM ".$table;
            $this->res = $this->con->query($sql);
            if($this->res->num_rows <= 0){
                return (json_encode(array("Error"=>"Sem Data nessa tabela !")));
            }
        }
        function getEntryFromFK($table,$fields){
            $this->settingData($table,$fields,$fields);
            $this->res->fetch_all(MYSQLI_ASSOC);
        }
        function getAllEntries($table,$idslug){
            $this->settingData($table,$idslug);
            return $this->res->fetch_all(MYSQLI_ASSOC);;
        }
        function getEntryById($table,$idslug,$id){
            $this->settingData($table,$idslug,$id);
            return $this->res->fetch_assoc();
        }
        function getEntriesByCustomSQL($sql){
            $this->startCon();
            $this->res = $this->con->query($sql);
            return $this->res->fetch_all(MYSQLI_ASSOC);
        }
        function changeCallStatus($id,$status){
            $this->startCon();
            $sql = "UPDATE chamados SET statuschamado = '".$status."' WHERE idChamado = ".$id."";
            if(!$this->con->query($sql)){
                echo json_encode(array("Error" => "SQLError"));
            }
            return true;
        }
        function setNewUser($username,$pass,$nome,$phone,$email,$bdate,$insp=null){
            $this->startCon();
            $pass = password_hash($pass,PASSWORD_DEFAULT);
            $sql = "";
            if($insp != null){
               $sql = ("INSERT INTO usuarios(username,senha,nome,email,nascimento,telefone,eInspetor,codInspetor) VALUES ('".$username."','".$pass."','".$nome."','".$email."','".$bdate."','".$phone."',true,'".$insp."')");
            }else{
               $sql = ("INSERT INTO usuarios(username,senha,nome,email,nascimento,telefone,eInspetor,codInspetor) VALUES ('".$username."','".$pass."','".$nome."','".$email."','".$bdate."','".$phone."',false,null)");
            }
            if(!$this->con->query($sql)){
                die ("Erro de sql");
            }
            return true;
        }
        function setNewCall($idUsu,$rua,$bairro,$numero,$anexo,$data,$obs){
            $this->startCon();
            $sql = "INSERT INTO chamados(idUsu,rua,bairro,numero,anexo,datachamado,observacoes,statuschamado) VALUES ('".$idUsu."','".$rua."','".$bairro."',".$numero.",'".$anexo."','".$data."','".$obs."','Aberto')";
            if(!$this->con->query($sql)){
                return false;
            } 
            return true;
        }
        function setNewComment($idUsu,$idChamado,$datachamado,$comentario){
            $this->startCon();
            $sql = "INSERT INTO comentarios(idUsu,datacomentario,comentario,idChamado) VALUES (".$idUsu.",'".$datachamado."','".$comentario."',".$idChamado.")";
            if(!$this->con->query($sql)){
                return false;
            }
            return true;
        }
    }
?>