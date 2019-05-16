#consul-template
1.安装consul 启动命令：consul agent -dev -data-dir /tmp/consul -node=s1 -bind=172.20.4.98 -ui -client 0.0.0.0（单机）
2.安装consul-template 启动命令：consul-template -config ./tmpl.json -once
-once:仅允许一次，不加会一直运行，如果报错会停止
启动多个配置文件: consul-template -config ./alertmanager-tmpl.json -config ./prometheus-tmpl.json
其中tmpl.json文件内容：
consul {
        address ="172.20.4.98:8500" ##consul地址
}

template {

source = "./alertmanager.ctmpl" ##生成文件的模板
destination = "/usr/local/prometheus/alertmanager/alertmanager.yml" ##生成的文件路径及文件名称
command = ""  ##可以是文件重新启动的命令 比如：nginx service reload 

}


获取consul的key/value值：
{{key "receivers"}}