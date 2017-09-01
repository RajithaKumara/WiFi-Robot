<!doctype html>
<html lang=''>
<head>
   <meta charset='utf-8'>
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <link rel="stylesheet" href="styles.css">
   <title>CSS MenuMaker</title>
</head>
<body>

<div id='cssmenu'>
<ul>
   <li><a href='index.php'>Home</a></li>
   <li><a href='product.php'>Products</a></li>
   <li><a href='company.php'>Company</a></li>
   <li class='active'><a href='contact.php'>Contact</a></li>
</ul>
</div>
<script>
    function func(){
        var x=document.getElementById('cssmenu');
        if (x.style.display==='none'){
            x.style.display='block';
        }else{
            x.style.display='none';
        }
    }
</script>

</body>
<html>
