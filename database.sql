USE master;
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'ShoppingServiceMVC')
BEGIN
    CREATE DATABASE ShoppingServiceMVC;
END
GO

USE ShoppingServiceMVC;
GO

-- 1. Bảng [User] phục vụ Đăng nhập, Đăng ký, Phân quyền (JPA Entity User)
IF OBJECT_ID('dbo.[User]', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.[User](
        [id] INT IDENTITY(1,1) PRIMARY KEY,
        [email] NVARCHAR(255) NOT NULL,
        [username] NVARCHAR(50) NOT NULL UNIQUE,
        [fullname] NVARCHAR(100) NOT NULL,
        [password] NVARCHAR(255) NOT NULL, -- lưu chuỗi đã băm PBKDF2 (pbkdf2$vòng lặp$salt$hash)
        [avatar] NVARCHAR(500) NULL,
        [roleid] INT NOT NULL DEFAULT 3, -- 1: Admin, 2: Manager, 3: User
        [phone] NVARCHAR(20) NULL,
        [createdDate] DATE NULL,
        [active] BIT NOT NULL DEFAULT 1,          -- 1: đã kích hoạt bằng OTP, 0: chưa kích hoạt
        [otpCode] NVARCHAR(10) NULL,              -- OTP hiện hành (kích hoạt / quên mật khẩu)
        [otpPurpose] NVARCHAR(20) NULL,           -- ACTIVATE | RESET
        [otpExpiry] DATETIME2 NULL,
        [otpAttempts] INT NOT NULL DEFAULT 0
    );
END
GO

-- Nâng cấp bảng [User] đã tồn tại từ bài tập 02 (chạy lặp lại nhiều lần vẫn an toàn)
IF COL_LENGTH('dbo.[User]', 'active') IS NULL
    ALTER TABLE dbo.[User] ADD [active] BIT NOT NULL CONSTRAINT DF_User_active DEFAULT 1;
GO
IF COL_LENGTH('dbo.[User]', 'otpCode') IS NULL
    ALTER TABLE dbo.[User] ADD [otpCode] NVARCHAR(10) NULL;
GO
IF COL_LENGTH('dbo.[User]', 'otpPurpose') IS NULL
    ALTER TABLE dbo.[User] ADD [otpPurpose] NVARCHAR(20) NULL;
GO
IF COL_LENGTH('dbo.[User]', 'otpExpiry') IS NULL
    ALTER TABLE dbo.[User] ADD [otpExpiry] DATETIME2 NULL;
GO
IF COL_LENGTH('dbo.[User]', 'otpAttempts') IS NULL
    ALTER TABLE dbo.[User] ADD [otpAttempts] INT NOT NULL CONSTRAINT DF_User_otpAttempts DEFAULT 0;
GO
ALTER TABLE dbo.[User] ALTER COLUMN [password] NVARCHAR(255) NOT NULL;
GO

-- 2. Bảng categories phục vụ CRUD JPA (JPA Entity Category)
IF OBJECT_ID('dbo.categories', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.categories(
        [categoryId] INT IDENTITY(1,1) PRIMARY KEY,
        [categoryname] NVARCHAR(255) NOT NULL,
        [images] NVARCHAR(500) NULL,
        [status] INT NOT NULL DEFAULT 1 -- 1: Hoạt động, 0: Khóa
    );
END
GO

-- 3. Bảng videos: quan hệ 1-N với categories và 1-N với [User] (mỗi user có nhiều video) (JPA Entity Video)
IF OBJECT_ID('dbo.videos', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.videos(
        [videoId] NVARCHAR(50) PRIMARY KEY,
        [active] BIT NOT NULL DEFAULT 1,
        [description] NVARCHAR(MAX) NULL,
        [poster] NVARCHAR(500) NULL,
        [title] NVARCHAR(255) NULL,
        [views] INT NOT NULL DEFAULT 0,
        [categoryId] INT FOREIGN KEY REFERENCES dbo.categories([categoryId]) ON DELETE CASCADE,
        [userId] INT NULL CONSTRAINT FK_videos_User FOREIGN KEY REFERENCES dbo.[User]([id]), -- video thuộc về user nào
        [createdDate] DATETIME2 NULL                                                        -- ngày đăng video
    );
END
GO

-- Nâng cấp bảng videos đã tồn tại: thêm khóa ngoại tới [User] và ngày đăng (chạy lặp lại vẫn an toàn)
IF COL_LENGTH('dbo.videos', 'userId') IS NULL
    ALTER TABLE dbo.videos ADD [userId] INT NULL CONSTRAINT FK_videos_User FOREIGN KEY REFERENCES dbo.[User]([id]);
GO
IF COL_LENGTH('dbo.videos', 'createdDate') IS NULL
    ALTER TABLE dbo.videos ADD [createdDate] DATETIME2 NULL;
GO

-- 4. Dữ liệu mẫu
-- Tài khoản Admin (mật khẩu 123456, đã băm PBKDF2 - không lưu plain-text)
IF NOT EXISTS (SELECT 1 FROM dbo.[User] WHERE [username] = 'admin')
BEGIN
    INSERT INTO dbo.[User] ([email], [username], [fullname], [password], [avatar], [roleid], [phone], [createdDate])
    VALUES (N'admin@gmail.com', N'admin', N'Trần Hữu Việt Huân', N'pbkdf2$65536$lOIhmultF59jMeogPlMLBg==$SNauMvCyXh8UlHbEaS40OtpZBPwYDX+k+WTEjIW/4Z4=', NULL, 1, N'0901234567', GETDATE());
END
ELSE
BEGIN
    UPDATE dbo.[User] 
    SET [fullname] = N'Trần Hữu Việt Huân', [password] = N'pbkdf2$65536$lOIhmultF59jMeogPlMLBg==$SNauMvCyXh8UlHbEaS40OtpZBPwYDX+k+WTEjIW/4Z4=', [roleid] = 1, [active] = 1 
    WHERE [username] = 'admin';
END

IF NOT EXISTS (SELECT 1 FROM dbo.[User] WHERE [username] = 'viethuan')
BEGIN
    INSERT INTO dbo.[User] ([email], [username], [fullname], [password], [avatar], [roleid], [phone], [createdDate])
    VALUES (N'viethuan@hcmute.edu.vn', N'viethuan', N'Trần Hữu Việt Huân', N'pbkdf2$65536$SoEWo/seHvaKHQJ/B3HvuA==$m5ddouPjHyv3kiYb6sDkuN9w/AgzLwY7yCXgVMO3VLM=', NULL, 1, N'0908617108', GETDATE());
END
ELSE
BEGIN
    UPDATE dbo.[User] 
    SET [fullname] = N'Trần Hữu Việt Huân', [password] = N'pbkdf2$65536$SoEWo/seHvaKHQJ/B3HvuA==$m5ddouPjHyv3kiYb6sDkuN9w/AgzLwY7yCXgVMO3VLM=', [roleid] = 1, [active] = 1 
    WHERE [username] = 'viethuan';
END
GO

-- Dữ liệu mẫu danh mục JPA
IF NOT EXISTS (SELECT 1 FROM dbo.categories WHERE [categoryname] = N'Điện thoại & Phụ kiện')
BEGIN
    INSERT INTO dbo.categories ([categoryname], [images], [status])
    VALUES 
    (N'Điện thoại & Phụ kiện', N'https://via.placeholder.com/200x150?text=Phone', 1),
    (N'Laptop & Thiết bị số', N'https://via.placeholder.com/200x150?text=Laptop', 1),
    (N'Thời trang & Làm đẹp', N'avatar.png', 1);
END
GO

SELECT * FROM dbo.[User];
SELECT * FROM dbo.categories;
SELECT * FROM dbo.videos;
GO
