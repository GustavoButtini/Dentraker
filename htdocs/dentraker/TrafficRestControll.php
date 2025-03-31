<?php 
    require_once ("UserRestControll.php");
    require_once ("CallRestControll.php");
    require_once ("CommentRestControll.php");
    require_once ("ImageHandler.php");
    $select ="";
    $type = "";
    if(isset($_GET["select"])){
        $select = $_GET["select"];
    }
    if(isset($_GET["type"])){
        $type = $_GET["type"];
    }
    switch ($select){
        case "":
            die ("Error 404");
            break;
        case "usuarios":
            $UserRestControll = new UserRestControll;
            switch($type){
                case "all":
                    $UserRestControll->getAllUsers();
                    break;
                case "single":
                    isset($_GET["id"]) ? $UserRestControll->getUserById($_GET["id"]) : die("Identificador não localizado");
                    break;
                case "insert":
                    if(isset($_POST["isInsp"]) && isset($_POST["username"]) && isset($_POST["pass"]) && isset($_POST["nome"]) && isset($_POST["phone"]) && isset($_POST["mail"]) && isset($_POST["bdate"]) && isset($_POST["isInsp"])){
                        if($_POST["isInsp"] === true){
                            $UserRestControll->insertData($_POST["username"],$_POST["pass"],$_POST["nome"],$_POST["phone"],$_POST["mail"],$_POST["bdate"],$_POST["inspCode"]);
                        }else{
                            $UserRestControll->insertData($_POST["username"],$_POST["pass"],$_POST["nome"],$_POST["phone"],$_POST["mail"],$_POST["bdate"]);
                        }
                    }else{
                        echo $_POST["isInsp"];
                    }
                    break;
                case "login":
                    if(isset($_POST["username"]) && isset($_POST["pass"])){
                        $UserRestControll->getUserLogin($_POST["username"],$_POST["pass"]);
                    }else{
                        echo "Erro de Params";
                    } 
                    break;
                case "commentators":
                    if(isset($_GET["id"])){
                        $UserRestControll->getCommentatorsByCall($_GET["id"]);
                        break;
                    }
            }       
            break;
        case "pontos":
            $CallRestControll = new CallRestControll;
            switch($type){
                case "all":
                    $CallRestControll->getAllCalls();
                    break;
                case "single":
                    if(isset($_GET["id"])){
                        $CallRestControll->getCallsByUser($_GET["id"]);
                    }else{
                        echo (array("Error" => "Sem Usuario com esse id"));
                    }
                    break;
                case "changestatus":
                    if(isset($_POST['id']) && isset($_POST['status'])){
                        $CallRestControll->setCallStatus($_POST['id'],$_POST['status']);
                    }else{
                        echo json_encode(array("Error" => "Sem Chamado com esse ID"));
                    }
                case "insert":
                    if(isset($_POST["idUsu"]) && isset($_POST["rua"]) && isset($_POST["bairro"]) && isset($_POST["numero"]) && isset($_POST["anexo"]) && isset($_POST["data"]) && isset($_POST["observacoes"])){
                        $CallRestControll->insertNewCall($_POST["idUsu"],$_POST["rua"],$_POST["bairro"],($_POST["numero"]),($_POST["anexo"]),($_POST["data"]),$_POST["observacoes"]);
                        break;
                    }
            }
            break;
        case "comentario":
            $CommentRestControll = new CommentRestControll;
            switch($type){
                case "all":
                    $CommentRestControll->getAllComments();
                    break;
                case "single":
                    if(isset($_GET["id"])){
                        $CommentRestControll->getCommentByCall($_GET["id"]);
                    }else{
                        echo (array("Error" => "Sem Chamado com esse id !"));
                        break;
                    }
                case "insert":
                    if(isset($_POST['idUsu']) && isset($_POST['datacomentario']) && isset($_POST['comentario']) && isset($_POST['idChamado'])){
                        $CommentRestControll->setNewComment($_POST['idUsu'],$_POST['datacomentario'],$_POST['comentario'],$_POST['idChamado']);
                        break;
                    }else{
                        echo (json_encode(array("Error" => "Not all params")));
                        break;
                    }
                }
            break;
        case "images":
            $imagehandler = new ImageHandler();
            switch($type){
                case "insert":
                    if(isset($_FILES['image'])){
                        $imagehandler->saveImageOnServer($_FILES['image']);
                        break;
                    }
            }
            break;
        }
?>