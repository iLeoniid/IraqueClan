# IraqueClan

Sistema de Clãs completo para **Minecraft Paper 1.21.4+**, desenvolvido para o **Iraque Survival**.

![Java](https://img.shields.io/badge/Java-25-orange)
![Paper](https://img.shields.io/badge/Paper-1.21.4+-green)
![Version](https://img.shields.io/badge/Version-1.0.0-blue)

---

## Dependencias

| Plugin | Tipo | Obligatorio |
|--------|------|:-----------:|
| [IraqueCore](https://github.com/iLeoniid/IraqueCore) | Hard | Si |
| [Vault](https://www.spigotmc.org/resources/vault.34315) | Soft | No |
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.13811) | Soft | No |

---

## Comandos

### Comando Principal `/clan`

| Comando | Descripcion | Permiso |
|---------|-------------|---------|
| `/clan` | Abre el menu principal | `iraqueclan.use` |
| `/clan criar <nome> <tag>` | Crea un clan | `iraqueclan.create` |
| `/clan convidar <jogador>` | Invita a un jugador | `iraqueclan.invite` |
| `/clan aceitar` | Acepta una invitacion | `iraqueclan.use` |
| `/clan sair` | Sal del clan | `iraqueclan.use` |
| `/clan expulsar <jogador>` | Expulsa un miembro | `iraqueclan.kick` |
| `/clan dissolver` | Disuelve el clan | `iraqueclan.disband` |
| `/clan promover <jogador>` | Promueve un miembro | `iraqueclan.promote` |
| `/clan rebaixar <jogador>` | Degradar un miembro | `iraqueclan.demote` |
| `/clan tag <tag>` | Cambia la tag del clan | `iraqueclan.settings` |
| `/clan cor <cor>` | Cambia el color del clan | `iraqueclan.settings` |
| `/clan desc <texto>` | Cambia la descripcion | `iraqueclan.settings` |
| `/clan icon <material>` | Cambia el icono del clan | `iraqueclan.settings` |
| `/clan banco depositar <valor>` | Deposita dinero al banco | `iraqueclan.use` |
| `/clan banco sacar <valor>` | Saca dinero del banco | `iraqueclan.use` |
| `/clan banco saldo` | Ver saldo del banco | `iraqueclan.use` |
| `/clan home` | Teletransporta a la base | `iraqueclan.use` |
| `/clan sethome` | Establece la base | `iraqueclan.use` |
| `/clan delhome <nome>` | Elimina una casa | `iraqueclan.use` |
| `/clan homes` | Lista las casas del clan | `iraqueclan.use` |
| `/clan mail send <texto>` | Envia un correo | `iraqueclan.use` |
| `/clan mail read` | Lee los correos | `iraqueclan.use` |
| `/clan mail clear` | Limpia los correos | `iraqueclan.use` |
| `/clan motd set <texto>` | Establece el MOTD | `iraqueclan.use` |
| `/clan motd clear` | Limpia el MOTD | `iraqueclan.use` |
| `/clan chat` | Activa/desactiva chat del clan | `iraqueclan.use` |
| `/clan loja` | Abre la tienda de upgrades | `iraqueclan.use` |
| `/clan quest list` | Ver misiones activas | `iraqueclan.use` |
| `/clan quest refresh` | Refresca las misiones | `iraqueclan.use` |
| `/clan conquista` | Ver logros del clan | `iraqueclan.use` |
| `/clan perfil <clan>` | Ver perfil de un clan | `iraqueclan.info` |
| `/clan logs` | Ver historial de acciones | `iraqueclan.use` |
| `/clan top <tipo>` | Ranking de clanes | `iraqueclan.list` |
| `/clan xp` | Ver XP del clan | `iraqueclan.use` |
| `/clan diplo` | Ver diplomacia del clan | `iraqueclan.use` |
| `/clan guerra <clan>` | Declarar guerra | `iraqueclan.war.declare` |
| `/clan guerra aceitar` | Aceptar guerra pendiente | `iraqueclan.war.accept` |
| `/clan guerra recusar` | Rechazar guerra pendiente | `iraqueclan.war.accept` |
| `/clan guerra render` | Rendirse en una guerra | `iraqueclan.war.accept` |
| `/clan guerra stats` | Estadisticas de guerra | `iraqueclan.war.accept` |

### Comando de Admin `/clana`

| Comando | Descripcion |
|---------|-------------|
| `/clana reload` | Recarga toda la configuracion |
| `/clana save` | Fuerza el guardado de datos |
| `/clana inspect <clan>` | Inspecciona un clan |
| `/clana vault <clan>` | Ver banco de un clan |
| `/clana eco <clan> <add\|remove\|set> <valor>` | Modificar economia |
| `/clana addxp <clan> <valor>` | Agregar XP a un clan |
| `/clana setlevel <clan> <nivel>` | Establecer nivel de un clan |
| `/clana setleader <clan> <jogador>` | Cambiar lider de un clan |
| `/clana forcejoin <clan> <jogador>` | Forzar entrada a un clan |
| `/clana disband <clan> [confirmar>` | Disolver un clan |
| `/clana rename <clan> <novo>` | Renombrar un clan |
| `/clana setholo <tipo>` | Configurar hologramas |
| `/clana endseason` | Finalizar temporada |

---

## Sistemas

### Sistema de Niveles y XP
El clan gana XP mediante acciones en el servidor:
- Matar mobs: configurable en config
- Matar jugadores: configurable en config
- Minerar bloques: configurable en config
- Y mas eventos (construccion, pesca, etc.)

Recompensas al subir de nivel (configurables):
- Dinero para el banco del clan
- Puntos de XP bonus
- Ejecucion de comandos personalizados

### Tienda de Upgrades
Mejoras disponibles para el clan:

| Upgrade | Efecto | Nivel Max | Precio Base |
|---------|--------|:---------:|------------:|
| Raio da Base | Aumenta area protegida | 5 | $5.000 |
| Boost de XP | +25% XP por nivel | 3 | $10.000 |
| Limite de Membros | +5 miembros por nivel | 5 | $8.000 |
| Casas Extras | +1 casa por nivel | 5 | $3.000 |
| Capacidade do Vault | +9 slots por nivel | 5 | $4.000 |

### Sistema de Guerras
- Declarar guerra a otro clan con apuestas
- Sistema de kills para determinar ganador
- Cooldown entre kills para evitar abusos
- Historial de guerras con estadisticas
- Opcion de rendirse

### Sistema de Diplomacia
- Alianzas entre clanes (max 3)
- Rivalidades entre clanes (max 3)
- Proteccion de dano entre aliados (configurable)
- Sistema de solicitudes con expiracion

### Sistema de Misiones (Quests)
106 misiones organizadas por categorias:

| Categoria | Misiones | Ejemplos |
|-----------|:--------:|----------|
| Matar Monstros | 20 | Matar Zumbis, Caçada Geral, Exterminio |
| Matar Jogadores | 8 | PvP Intenso, Gladiador, Assassino |
| Minerar | 11 | Minerar Diamante, Expedicao Mineira |
| Cortar Madeira | 10 | Desmatamento, Serrar Makeira |
| Colher Plantas | 10 | Colheita Geral, Farm Intenso |
| Construir | 5 | Construir Base, Obra Grande |
| Plantar | 5 | Plantar Arvores, Plantio Massivo |
| Pescar | 5 | Pescaria Profissional |
| Domar Animais | 5 | Domar Cavalos, Colecionar Animais |
| Usar Arco | 5 | Arqueiro, Chuva de Flechas |
| Craftar | 5 | Mestre Crafter, Fabrica Total |
| Encantar | 5 | Mago do Encantamento |
| Comer | 5 | Banquete, Gastronomico |
| Beber | 4 | Pocoes de Combate |
| Usar Balde | 3 | Obra Hidraulica |

### Sistema de Conquistas (Achievements)
Logros que se desbloquean automaticamente:
- **Primeiro Sangue** - Primera kill del clan
- **Senhor da Guerra** - Ganar 10 guerras
- **Rico** - Tener $100.000 en el banco
- **Unidos** - Tener 20 miembros
- **Lendario** - Alcanzar nivel 50

### Sistema de Logros (Homes)
- Hasta 5 casas por clan (mejorables)
- Cooldown de teletransporte configurable
- Delay de teletransporte con countdown

### Sistema de Correo
- Enviar/recibir mensajes entre clanes
- Limpiar buzón de correos

### Sistema de MOTD
- Mensage del dia que se muestra al entrar al clan

### Sistema de Chat
- Chat dedicado para miembros del clan
- Formato personalizable con placeholders

### Sistema de Logs
- Registro de acciones: depositos, retiros, kicks, promociones, guerras, diplomacia, tienda, creacion, dissolution, entradas y salidas
- Maximo 100 logs almacenados

### Sistema de Perfil
- Descripcion personalizable por clan
- Icono personalizable (material de Minecraft)

### Sistema de Ranking (Top)
6 tipos de ranking:
- Kills totales
- Nivel
- Banco (dinero)
- KDR (Kill/Death Ratio)
- Miembros
- Tiempo de existencia

---

## Placeholders (PlaceholderAPI)

| Placeholder | Descripcion |
|-------------|-------------|
| `%iraqueclan_clan%` | Nombre del clan |
| `%iraqueclan_tag%` | Tag del clan |
| `%iraqueclan_tag_formatted%` | Tag formateado con color |
| `%iraqueclan_leader%` | Nombre del lider |
| `%iraqueclan_members%` | Cantidad de miembros |
| `%iraqueclan_max_members%` | Limite de miembros |
| `%iraqueclan_level%` | Nivel del clan |
| `%iraqueclan_xp%` | XP actual |
| `%iraqueclan_kills%` | Kills totales |
| `%iraqueclan_deaths%` | Muertes totales |
| `%iraqueclan_kdr%` | Kill/Death Ratio |
| `%iraqueclan_bank%` | Saldo del banco |
| `%iraqueclan_war_wins%` | Guerras ganadas |
| `%iraqueclan_war_losses%` | Guerras perdidas |
| `%iraqueclan_war_draws%` | Guerras empatadas |
| `%iraqueclan_role%` | Rol del jugador |
| `%iraqueclan_description%` | Descripcion del clan |
| `%iraqueclan_homes%` | Cantidad de casas |
| `%iraqueclan_max_homes%` | Limite de casas |
| `%iraqueclan_in_clan%` | Si esta en un clan (true/false) |

---

## Permisos

| Permiso | Descripcion | Default |
|---------|-------------|:-------:|
| `iraqueclan.*` | Todas las permisos | OP |
| `iraqueclan.use` | Comandos basicos | true |
| `iraqueclan.create` | Crear clan | true |
| `iraqueclan.invite` | Invitar jugadores | true |
| `iraqueclan.kick` | Expulsar jugadores | true |
| `iraqueclan.disband` | Disolver clan | true |
| `iraqueclan.promote` | Promover miembros | true |
| `iraqueclan.demote` | Degradar miembros | true |
| `iraqueclan.war.declare` | Declarar guerra | true |
| `iraqueclan.war.accept` | Aceptar guerra | true |
| `iraqueclan.info` | Ver info de clanes | true |
| `iraqueclan.list` | Listar clanes | true |
| `iraqueclan.settings` | Configurar clan | true |
| `iraqueclan.admin` | Comandos admin | OP |

---

## Integraciones

| Plugin | Funcion |
|--------|---------|
| **Vault** | Sistema economico (depositos, retiros, tienda, apuestas) |
| **PlaceholderAPI** | 20+ placeholders para integracion con TAB, Discord, etc. |
| **WorldGuard** | Bloquear acciones en regiones protegidas |
| **Discord** | Webhooks para eventos de clan (crear, disolver, guerras) |

---

## Configuracion

Todos los textos estan en **portugues (PT-BR)** y son 100% editables en `messages.yml`. Los colores usan formato hex `&#RRGGBB`.

Archivos de configuracion:
- `config.yml` - Configuracion principal del plugin
- `messages.yml` - Todos los mensajes del sistema
- `quests.yml` - Definicion de las 106 misiones

---

## Instalacion

1. Descarga `IraqueClan.jar`
2. Colocalo en la carpeta `plugins/`
3. Asegurate de tener **IraqueCore** instalado
4. (Opcional) Instala **Vault** y/o **PlaceholderAPI**
5. Reinicia el servidor
6. Configura `config.yml` segun tus necesidades

---

## Compilacion

```bash
# Requiere Java 25
export JAVA_HOME="/ruta/a/jdk-25"
./gradlew build
```

El `.jar` generado estara en `build/libs/`.

---

Hecho por **Leo**

Total de codigo: **9.670 lineas**
