<?php 
    require_once("SimpleRest.php");
    
    class ImageHandler extends SimpleRest{
        function saveImageOnServer($img){
            $dir = 'images/';
            $filename = uniqid() . '-' . basename($img['name']);
            $path = $dir . $filename;
            if(!in_array($img['type'],['image/jpg','image/png'])){
                $statuscode = 400;
                $res = array("Erro" => "Imagem fora de formatação ideal");
            }
            // 1024 = Byte , 1024 * 1024 = MegaByte , 5 * 1024 * 1024 = 5 MegaBytes
            if($img['size'] > 5 * 1024 * 1024){
                $statuscode = 400;
                $res = array("Erro" => "A Imagem deve ter menos de 5 MB");
            }
            if(move_uploaded_file($img['tmp_name'],$path)){
                $statuscode = 200;
                $res = array ("Sucess"=> $path);
            }
            $contenttype = @$_SERVER["HTTP_ACCEPT"];
            $this->setHttpHeaders($contenttype,$statuscode);
            if(strpos($contenttype,'application/json') !== false){
                echo (json_encode($res));
            }
        }
    }
?> 