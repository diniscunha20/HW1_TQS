#!/bin/bash

# Script para gerenciar o container PostgreSQL do projeto Zeromonos

CONTAINER_NAME="zeromonos_db"
DB_USER="admin"
DB_PASSWORD="secret"
DB_NAME="zeromonos_db"
DB_PORT="5432"

case "$1" in
    start)
        echo "🚀 Iniciando container PostgreSQL..."
        if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
            docker start $CONTAINER_NAME
            echo "✅ Container iniciado!"
        else
            echo "⚠️  Container não existe. Criando..."
            docker run --name $CONTAINER_NAME \
                -e POSTGRES_USER=$DB_USER \
                -e POSTGRES_PASSWORD=$DB_PASSWORD \
                -e POSTGRES_DB=$DB_NAME \
                -p $DB_PORT:5432 \
                -d postgres:latest
            echo "✅ Container criado e iniciado!"
        fi
        ;;
    
    stop)
        echo "🛑 Parando container PostgreSQL..."
        docker stop $CONTAINER_NAME
        echo "✅ Container parado!"
        ;;
    
    restart)
        echo "🔄 Reiniciando container PostgreSQL..."
        docker restart $CONTAINER_NAME
        echo "✅ Container reiniciado!"
        ;;
    
    status)
        echo "📊 Status do container:"
        docker ps -a --filter "name=$CONTAINER_NAME" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
        ;;
    
    logs)
        echo "📋 Logs do container:"
        docker logs $CONTAINER_NAME --tail 50 --follow
        ;;
    
    connect)
        echo "🔗 Conectando ao PostgreSQL..."
        docker exec -it $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME
        ;;
    
    remove)
        echo "⚠️  ATENÇÃO: Isso vai remover o container e TODOS OS DADOS!"
        read -p "Tem certeza? (s/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Ss]$ ]]; then
            docker stop $CONTAINER_NAME 2>/dev/null
            docker rm $CONTAINER_NAME
            echo "✅ Container removido!"
        else
            echo "❌ Operação cancelada."
        fi
        ;;
    
    *)
        echo "🐘 Gerenciador do PostgreSQL - Projeto Zeromonos"
        echo ""
        echo "Uso: $0 {start|stop|restart|status|logs|connect|remove}"
        echo ""
        echo "Comandos:"
        echo "  start    - Inicia o container (cria se não existir)"
        echo "  stop     - Para o container"
        echo "  restart  - Reinicia o container"
        echo "  status   - Mostra o status do container"
        echo "  logs     - Mostra os logs do container"
        echo "  connect  - Conecta ao PostgreSQL via psql"
        echo "  remove   - Remove o container (APAGA OS DADOS!)"
        echo ""
        exit 1
        ;;
esac

exit 0
