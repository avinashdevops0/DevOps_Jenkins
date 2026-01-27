# Jenkins Security: Project-Based Matrix Authorization

---

## **1. Enable Project-Based Matrix Authorization**
1. Open Jenkins → **Manage Jenkins → Configure Global Security**  
2. Under **Authorization**, select:  
   **Project-based Matrix Authorization Strategy**  

---

## **2. Add Users**
1. Go to **Manage Jenkins → Manage Users → Create User** (if not already created)  
2. Add the desired users to Jenkins  

---

## **3. Configure Job-Level Permissions**
1. Open the Jenkins job you want to secure → **Configure**  
2. Scroll to **Enable project-based security** → Check the box  
3. **Add User** → Assign permissions for that specific job:  
   - Read, Build, Configure, Delete, etc.  

---

## **4. Notes / Best Practices**
- Use **project-based matrix** to give fine-grained control over job access  
- Always assign **minimum required permissions** to each user  
- Test permissions by logging in as a restricted user before going live  
- Combine with global roles for easier management of multiple jobs  

