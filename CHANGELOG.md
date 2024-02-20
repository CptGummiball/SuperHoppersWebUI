# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - 2024-02-20

### Added
- Worth Script for Mob Hoppers

### Fixed
- config.json will now be copied to web folder

### Changed
- Logger will now clear files when above 5mb
- Added UserName to Link command

## [Unreleased] - 2024-02-14

### Added
- Commands
- Translation for Filters

### Fixed
- FileGenerator
  - List Cleaner now includes "_"

## [Unreleased] - 2024-02-14

### Added

- DebugLog
	- Documents every process of the FileGenerator
- DebugMode
	- Outputs the FileGenerator process step by step in the console
- More Webfiles
- First index file added (just my template)

### Fixed

- FileGenerator
	- Now creates a cleaner json with more relevant data (Item worth and more). Mob hoppers continued to only specify type and quantity
- The plugin instance was loaded incorrectly, the problem has now been resolved
- Translator and Config Sync are deactivated for now

## [Unreleased] - 2024-02-11

### Added

- Basic framework for further development on the Java part
- FileGenerator
	- FileGenerator organizes and processes YAML files, creates folder structure and saves JSON output for each OwnerUUID. It also will be used for updating files.
- Empty Templates
- Item Icons
