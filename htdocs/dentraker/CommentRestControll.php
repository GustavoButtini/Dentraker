<?php 
    require_once ("connection.php");
    require_once ("SimpleRest.php");
    class CommentRestControll extends SimpleRest{

        function getAllComments(){
            $con = new Connection();
            $data = $con->getAllEntries("comentarios","idComentario");
            if(empty($data)){
                $statuscode = 404;
                $data = array("Error"=>"Sem Comentarios cadastrados !");
            }else{
                $statuscode = 200;
            }
            $contenttype = @$_SERVER['HTTP_ACCEPT'];
            $this->setHttpHeaders($contenttype,$statuscode);
            if(strpos($contenttype,'application/json') !== false){
                echo (json_encode($data));
            }
        }
        function getCommentByCall($id){
            $con = new Connection();
            $data = $con->getEntriesByCustomSQL("SELECT * FROM comentarios WHERE idChamado = ".$id."");
            if(empty($data)){
                $statuscode = 404;
                $data = array("NoData" => "Esse chamado ainda não foi comentado !");
            }else{
                $statuscode = 200; 
            }
            $contenttype = @$_SERVER['HTTP_ACCEPT'];
            $this->setHttpHeaders($contenttype,$statuscode);
            if(strpos($contenttype,'application/json') !== false){
                echo (json_encode($data));
            }
        }
        function setNewComment($user,$data,$txt,$call){
            $con = new Connection();
            $res = $con->setNewComment($user,$call,$data,$txt);
            if(!$res){
                $statuscode = 404;
                $data = array("Error" => "Comment not created");
            }else{
                $statuscode = 200;
                $data = array("Sucess" => "Data Created");
            }
            $contenttype = @$_SERVER['HTTP_ACCEPT'];
            $this->setHttpHeaders($contenttype,$statuscode);
            if(strpos($contenttype,'application/json') !== false){
                echo (json_encode($data));
            }
        }
    }
?>