# Jenkins ThinBackup Plugin Notes

---

## 1. Install ThinBackup Plugin
1. Open Jenkins → **Manage Jenkins** → **Manage Plugins**  
2. Go to **Available** tab → Search for `ThinBackup`  
3. Install the plugin and restart Jenkins if required  

---

## 2. Configure Backup Settings
1. Navigate to: **Manage Jenkins → Configure System → ThinBackup**  
2. **Backup Directory:**  
   - Specify the path where backups will be stored (e.g., `/var/jenkins_backup`)  
3. **Move Old Backups to ZIP:**  
   - **Enable/Check** this option to automatically compress old backups into ZIP files  
4. Optional: Configure **Backup Schedule** using cron syntax for automatic backups  

---

## 3. Access Backups
1. Go to **Manage Jenkins → ThinBackups** (usually listed at the bottom of the Manage page)  
2. From here, you can:  
   - View existing backups  
   - Restore a previous backup  
   - Delete old backups  

---

## 4. Notes / Best Practices
- Ensure backup directory permissions allow Jenkins to write files  
- Enable ZIP compression to save disk space for older backups  
- Regularly monitor backup logs to verify successful backups  
- Test restore process periodically to ensure backup integrity
