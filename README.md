# FriendLock
Aplikasi android untuk melakukan pemblokiran aplikasi lain dan mendelegasikan kontrol pembukaan aplikasi kepada orang lain.

## Table of Contents
- [Deskripsi Singkat](#deskripsi-singkat)
- [Fitur](#fitur)
- [Screenshots](#screenshots)
- [Cara Build Aplikasi](#cara-menjalankan-aplikasi)

## Deskripsi Singkat
**FriendLock** adalah aplikasi Android yang memungkinkan pengguna untuk memblokir akses ke aplikasi-aplikasi tertentu. Untuk membuka aplikasi yang diblokir, pengguna memerlukan OTP (One-Time Password) yang diberikan oleh orang yang dipercaya (keyholder). Dengan demikian, kontrol akses aplikasi dapat didelegasikan untuk membantu mengurangi penggunaan aplikasi secara impulsif.

## Fitur
- App block
- Unlock via OTP
- Generate OTP

## Screenshots
![image](https://github.com/user-attachments/assets/b2290823-7e3b-4eed-bbb0-ded8915c86f6)

![image](https://github.com/user-attachments/assets/c8460bc1-ba7b-4522-b279-402d69dd9e9b)

![image](https://github.com/user-attachments/assets/69c2e512-b888-4503-a439-a23adfe9e534)

![image](https://github.com/user-attachments/assets/2f0bbc78-3846-47c1-93a3-3e4e3c218583)

![image](https://github.com/user-attachments/assets/04d8e341-b5b7-40e8-914d-47633c40b6f3)




## Cara Build Aplikasi
1. Clone repository:
   ```bash
   git clone https://github.com/hannoobz/FriendLock-CapstoneProject.git
2. Masuk ke folder repository
   ```bash
   cd FriendLock-CapstoneProject
3. Buat script gradlew menjadi executable
   ```bash
   sudo chmod +x ./gradlew
4. Build project menggunakan gradlew
   ```bash
   ./gradlew assembleRelease
