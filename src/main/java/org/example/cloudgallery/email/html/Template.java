package org.example.cloudgallery.email.html;

public class Template {
    public static final String REGISTER = """
                <!DOCTYPE html>
                <html lang="zh-CN">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>验证您的邮箱地址</title>
                    <style>
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol', 'Noto Color Emoji';
                            background-color: #f3f4f6;
                            margin: 0;
                            padding: 0;
                            -webkit-font-smoothing: antialiased;
                            -moz-osx-font-smoothing: grayscale;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background-color: #ffffff;
                            border-radius: 8px;
                            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                            overflow: hidden;
                        }
                        .header {
                            background-color: #4f46e5; /* 可替换为您的品牌主色调 */
                            padding: 24px;
                            text-align: center;
                        }
                        .header img {
                            max-width: 150px;
                        }
                        .content {
                            padding: 32px;
                            text-align: center;
                        }
                        .content h1 {
                            color: #1f2937;
                            font-size: 24px;
                            font-weight: 600;
                            margin-top: 0;
                            margin-bottom: 16px;
                        }
                        .content p {
                            color: #4b5563;
                            font-size: 16px;
                            line-height: 1.5;
                            margin-bottom: 24px;
                        }
                        .verification-code {
                            display: inline-block;
                            background-color: #f3f4f6;
                            color: #1f2937;
                            font-size: 36px;
                            font-weight: 700;
                            letter-spacing: 8px;
                            padding: 16px 24px;
                            border-radius: 8px;
                            margin-bottom: 24px;
                        }
                        .cta-button {
                            display: inline-block;
                            background-color: #4f46e5; /* 可替换为您的品牌主色调 */
                            color: #ffffff;
                            font-size: 16px;
                            font-weight: 600;
                            text-decoration: none;
                            padding: 14px 28px;
                            border-radius: 8px;
                            transition: background-color 0.3s ease;
                        }
                        .cta-button:hover {
                            background-color: #4338ca; /* 可替换为您的品牌悬停色 */
                        }
                        .footer {
                            background-color: #f9fafb;
                            padding: 24px;
                            text-align: center;
                            font-size: 12px;
                            color: #6b7280;
                        }
                        .footer a {
                            color: #4f46e5;
                            text-decoration: none;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <!-- 在此处替换为您自己的Logo图片地址 -->
                            <img src="https://via.placeholder.com/150x50.png?text=您的Logo" alt="公司Logo">
                        </div>
                        <div class="content">
                            <h1>验证您的邮箱地址</h1>
                            <p>感谢您的注册！请使用以下验证码来完成您的账户创建。该验证码将在 <strong>5分钟</strong> 内失效。</p>
                            <div class="verification-code">
                                <!-- 此处为动态生成的验证码 -->
                                yourCode
                            </div>
                        </div>
                        <div class="footer">
                            <p>如果您没有请求此验证码，请忽略此邮件。</p>
                            <p>© 2025 Dingwan. 保留所有权利。</p>
                            <p><a href="#">我的网站</a> • <a href="#">联系我</a></p>
                        </div>
                    </div>
                </body>
                </html>
            
            """;

    public static String getRegisterTemplate(String code) {
        return REGISTER.replace("yourCode", code);
    }
}
