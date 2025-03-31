<?php 
    require_once("connection.php");
    require_once("SimpleRest.php");

    class CallRestControll extends SimpleRest{
        function getAllCalls(){
            $con = new Connection();
            $data = $con->getAllEntries("chamados","idChamado");
            if(empty($data)){
                $statuscode = 200;
                $data = array(
                    'Error' => 'No calls'
                );
            }else{
                $statuscode = 202;
            }
            $contenttype = @$_SERVER['HTTP_ACCEPT'];
            $this->setHttpHeaders($contenttype,$statuscode);
            if(strpos($contenttype,'application/json') !== false){
                echo (json_encode($data));
            }
        }
        function getCallsByUser($id){
            $con = new Connection();
            $data = $con->getEntriesByCustomSQL("SELECT * FROM Chamados WHERE idUsu = '".$id."'");
            if(empty($data)){
                $statuscode = 200;
                $data = array(
                    "Error" => "No Data found"
                );
            }else{
                $statuscode = 202;   
            }
            $contenttype = @$_SERVER['HTTP_ACCEPT'];
            $this->setHttpHeaders($contenttype,$statuscode);
            if(strpos($contenttype,'application/json') !== false){
                echo (json_encode($data));
            }
        }
        function setCallStatus($id,$status){
            $con = new Connection();
            $data = $con->changeCallStatus($id,$status);
            if(!$data){
                $statuscode = 404;
                $data = array("Error"=>"Não Foi possivel realizar a mudança");
            }else{
                $statuscode = 202;
            }
            $contenttype = @$_SERVER['HTTP_ACCEPT'];
            $this->setHttpHeaders($contenttype,$statuscode);
            if(strpos($contenttype,'application/json') !== false){
                echo(json_encode(array("Sucess"=>"A Mudanca foi realizada")));
            }
        }
        function insertNewCall($idUsu,$rua,$bairro,$numero,$anexo,$data,$obs){
            $con = new Connection();
            $res = $con->setNewCall($idUsu,$rua,$bairro,$numero,$anexo,$data,$obs);
            if(!$res){
                $statuscode = 404;
                $data = array("Error" => "Call not inserted");
            }else{
                $statuscode = 200;
            }
            $contenttype = @$_SERVER['HTTP_ACCEPT'];
            $this->setHttpHeaders($contenttype,$statuscode);
            echo (json_encode(array("Sucess" => "Call Inserted")));
        }
    }
?>