function [] = procwav(path)

Q = dir([path '\' '*.wav']);

for i = 1:length(Q)
    
    if (isdir(Q(i).name))
        %wav2bmp([path '\' Q(i).name])
        continue;
    end
    
    filename = Q(i).name;  %[path '\' Q(i).name];
    
    [path '\' filename]
    
    I = abs(specgram(wavread([path '\' filename]), 512, 64, 256));

    I = (I - min(min(I)));
    I = I/(mean(max(I)));

    I = flipud(I);

    [h w] = size(I);

    fid = fopen([path '\' filename '.txt'], 'w');
    fprintf(fid, '%d\n%d\n', [h w]);
    fprintf(fid, '%f\n', I');
    fclose(fid);
    imwrite(I, [path '\' filename '.png']);
end

