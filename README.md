
# Super Hoppers Web UI

[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE) [![Spigot](https://img.shields.io/badge/Spigot-orange.svg)](https://www.spigotmc.org) ![Underdev](https://img.shields.io/badge/in%20development-red.svg)

## Project Overview
SuperHoppers Web UI is a user-friendly web interface designed for the Spigot plugin [SuperHoppers](https://www.spigotmc.org/resources/superhoppers.97483/) created by [Bestem0r](https://github.com/Bestem0r). The primary goal of this project is to provide a simple and intuitive way for users to manage and monitor their Super Hoppers within the Minecraft server environment.
## Features

**Live Data:** Nearly Real-time information to keep users updated on the status and performance of their Super Hoppers.

**Internal Web Server:** The project includes an internal web server to facilitate communication and data transfer between the Minecraft server and the web interface.

**Multi-Language Support:** Language can be changed in the `config.json` included in the web folder (Currently `en` and `de`)

**Item Icons:** Integrated icons for a visual representation of items

## Setup

1. **Install SuperHoppers:**
Download the SuperHoppers plugin and follow the installation instructions.

2. **Web Interface Setup:**

- Download the latest version of the SuperHoppersWebUI.jar file from the releases page.
- Place it in the plugins folder of your Spigot server.
- Start or restart your Spigot server.
- Adjust settings such as server connection details, user preferences, and language selection in the configuration file:

````
# Configuration file for SuperHoppersWebUI
# Port for the web server
Port: 8080
# Update interval in seconds
Interval: 180
# Debug Mode (It will show more information in the console, but will also spam your server console)
DebugMode: false


#versions (don't touch)
versions:
    config: 1
    web: 1
````
- Restart your server to apply the settings

## WebUI
- The WebUI can be accessed via a web browser with the address `yourserverip:port`
- You can also get a clickable link via ingame command `/sui link`
- You can restart the WebUI to sync currency settings via ingame command `/sui restart`
- Currency symbol depends on your Super Hoppers settings

## Contributing

We welcome contributions and feedback from the community. Feel free to open issues, submit pull requests, or reach out to the project maintainers.


## Authors

For any inquiries or collaboration requests, you can reach out to the project authors:
- CptGummiball - [GitHub Profile](https://github.com/CptGummiball)


## Credits

- [SuperHoppers Plugin](https://www.spigotmc.org/resources/superhoppers.97483/) by [Bestem0r](https://github.com/Bestem0r)
## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details. The MIT License is a permissive open-source license that allows for flexibility in how the code can be used and shared.


