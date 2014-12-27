using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Reflection;
using System.Runtime.InteropServices;

namespace TextureMaker
{
    class Program
    {
        private const string _metalsFolderPath = @"D:\Temp\Metals";
        private const string _outputFolderPath = @"D:\Temp\Output";
        private const string _folderName = @"..\..\..\Resources";

        private static string[] _metalFiles;
        private static Image[] _metalImages;

        static void Main(string[] args)
        {
            _metalFiles = Directory.GetFiles(_metalsFolderPath);
            _metalImages = new Image[_metalFiles.Length];

            for (int i = 0; i < _metalFiles.Length; i++)
            {
                using (Image source = Image.FromFile(_metalFiles[i]))
                {
                    //Bitmap bmp = new Bitmap(32, 32);
                    Bitmap bmp = new Bitmap(16, 16);

                    using (Graphics g = Graphics.FromImage(bmp))
                    {
                        //g.DrawImage(source, 0, 0, 32, 32);
                        g.DrawImage(source, 0, 0, 16, 16);
                    }

                    _metalImages[i] = bmp;
                }
            }

            //CreateImages("Coin");
            //CreateImages("AnvilDie");
            //CreateImages("Flan");
            //CreateImages("Trussel");
            //CreateImages("TrusselWithDie");
            //CreateImages("AnvilDieSide");
            CreateImages("AnvilDieTop");
        }

        static void CreateImages(string entityName)
        {
            string mainFilePath = CreateFilePath(entityName + ".png");
            string overlayFilePath = CreateFilePath(entityName + "_Overlay.png");

            using (Image mainImage = Image.FromFile(mainFilePath), overlayImage = Image.FromFile(overlayFilePath))
            {
                for(int i = 0; i < _metalImages.Length; i++)
                {
                    Image metalImage = _metalImages[i];

                    using (Bitmap bmp = new Bitmap(metalImage))
                    {
                        ClearPixels(bmp, (Bitmap)overlayImage);

                        using (Graphics g = Graphics.FromImage(bmp))
                        {
                            g.DrawImageUnscaled(mainImage, 0, 0);
                        }

                        bmp.Save(CreateOutputFilePath(entityName, _metalFiles[i]), ImageFormat.Png);
                    }
                }
            }
        }

        static void ClearPixels(Bitmap bmp, Bitmap overlay)
        {
            BitmapData bmpData = bmp.LockBits(new Rectangle(0, 0, bmp.Width, bmp.Height), ImageLockMode.ReadWrite, PixelFormat.Format32bppArgb);
            byte[] bmpBytes = new byte[bmpData.Stride * bmp.Height];
            Marshal.Copy(bmpData.Scan0, bmpBytes, 0, bmpBytes.Length);

            BitmapData overlayData = overlay.LockBits(new Rectangle(0, 0, overlay.Width, overlay.Height), ImageLockMode.ReadOnly, PixelFormat.Format32bppArgb);
            byte[] overlayBytes = new byte[overlayData.Stride * overlay.Height];
            Marshal.Copy(overlayData.Scan0, overlayBytes, 0, overlayBytes.Length);

            for (Int32 i = 0; i < bmpBytes.Length; i += 4)
            {
                if (overlayBytes[i + 3] != 0)
                    continue;

                bmpBytes[i] = 0;
                bmpBytes[i + 1] = 0;
                bmpBytes[i + 2] = 0;
                bmpBytes[i + 3] = 0;
            }

            Marshal.Copy(bmpBytes, 0, bmpData.Scan0, bmpBytes.Length);

            bmp.UnlockBits(bmpData);

            overlay.UnlockBits(overlayData);
        }

        static string CreateOutputFilePath(string entityName, string metalFilePath)
        {
            string fileName = entityName + Path.GetFileName(metalFilePath).Replace(" ", "");

            return Path.Combine(_outputFolderPath, fileName);
        }
        
        static string CreateFilePath(string fileName)
        {
            return Path.Combine(Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location), _folderName, fileName);
        }
    }
}